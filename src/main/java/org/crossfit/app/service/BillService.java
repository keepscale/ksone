package org.crossfit.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.crossfit.app.domain.Bill;
import org.crossfit.app.domain.BillLine;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.BillStatus;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.repository.BillRepository;
import org.crossfit.app.repository.BillsBucket;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class BillService {

    private final Logger log = LoggerFactory.getLogger(BillService.class);


    @Inject
    private CrossFitBoxSerivce boxService;
    @Inject
    private MembershipService membershipService;

    @Inject
    private BookingRepository bookingRepository;
    
	@Autowired
	private BillRepository billRepository;
		
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	public Page<Bill> findBills(String search, Pageable pageable) {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		
		return billRepository.findAll(box, search, new HashSet<>(Arrays.asList(BillStatus.values())), true, pageable);
	}

	public int generateBill(LocalDate since, LocalDate until, int dayOfMonth, BillStatus withStatus) {
		return generateBill(since, until, dayOfMonth, withStatus, billRepository);
	}

	public int generateBill(LocalDate since, LocalDate until, int dayOfMonth, BillStatus withStatus, BillsBucket bucket) {

		log.info("Genreation des factures depuis le {} jusqu'au {} au {} de chaque mois avec le statut {} et le paiement {}", 
				since, until, dayOfMonth, withStatus);

		CrossFitBox box = boxService.findCurrentCrossFitBox();
		
		int counter = 0;
		since = since.withDayOfMonth(dayOfMonth);
		while (since.isBefore(until)) {
			Long nextBillCounter = findLastBillCountNumberInYear(since.getYear(), since.getMonthOfYear(), box, bucket) + 1;
			
			counter = counter + generateBill(since, withStatus, box, nextBillCounter, bucket);
			since = since.plusMonths(1);
		}
		return counter;
	}
	private int generateBill(LocalDate dateAt, BillStatus withStatus, CrossFitBox box, Long nextBillCounter, BillsBucket bucket) {

		log.info("Genreation des factures au {} avec le statut {}", dateAt, withStatus);
		
		Set<Subscription> subscriptionToBill = subscriptionRepository.findAllByBoxAtDate(box, dateAt);
		
		Map<Member, List<Subscription>> subscriptionByMember = subscriptionToBill.stream().collect(Collectors.groupingBy(Subscription::getMember));

		LocalDate firstDayOfMonth = dateAt.withDayOfMonth(1);
		LocalDate lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1);
		
		int counter = 0;
		for (Member m : subscriptionByMember.keySet()) {
			
			
			
			List<Subscription> subs = subscriptionByMember.get(m);
			for (Subscription sub : subs) {

				List<BillLine> lines = new ArrayList<>();
				LocalDate startDateBill = sub.getSubscriptionStartDate().isBefore(firstDayOfMonth) ? firstDayOfMonth : sub.getSubscriptionStartDate();
				LocalDate endDateBill = sub.getSubscriptionEndDate().isAfter(lastDayOfMonth) ? lastDayOfMonth : sub.getSubscriptionEndDate();
						
				BillLine line = new BillLine();
				line.setLabel(sub.getMembership().getName());
				line.setQuantity(1.0);
				if (this.membershipService.isMembershipPaymentByMonth(sub.getMembership())) {
					line.setPriceTaxIncl(sub.getMembership().getPriceTaxIncl());
				}
				else if (sub.getSubscriptionStartDate().isAfter(firstDayOfMonth.minusDays(1))){
					line.setPriceTaxIncl(sub.getMembership().getPriceTaxIncl()); //On facture si la date d'abo est après le mois
				}
				else {
					line.setPriceTaxIncl(0); //On facture pas un truc non recurrent les autres mois
				}
				line.setTaxPerCent(sub.getMembership().getTaxPerCent());
				
				line.setSubscription(sub, startDateBill, endDateBill);
				line.setTotalBooking(bookingRepository.countBySubscriptionBefore(sub, endDateBill.toDateTimeAtStartOfDay()));
				line.setTotalBookingOnPeriod(bookingRepository.countBySubscriptionBetween(sub, startDateBill.toDateTimeAtStartOfDay(), endDateBill.toDateTimeAtStartOfDay()));
				
				lines.add(line);
				
				LocalDate billDate = dateAt.isBefore(startDateBill) ? startDateBill : dateAt;

				saveAndLockBill(box, nextBillCounter, m, withStatus, billDate, billDate, sub.getPaymentMethod(), lines, bucket);
				nextBillCounter++;
				counter++;
			}
		}
		return counter;
	}


	public Bill saveAndLockBill(CrossFitBox box, Member member, BillStatus status, PaymentMethod paymentMethod,
			LocalDate effectiveDate, LocalDate payAtDate, List<BillLine> lines) {
		return saveAndLockBill(box, null, member, status, effectiveDate, payAtDate, paymentMethod, lines, billRepository);
	}
	
	private Bill saveAndLockBill(CrossFitBox box, Long nextBillCounter, Member member, BillStatus withStatus, LocalDate dateAt, LocalDate payAtDate, PaymentMethod paymentMethod, List<BillLine> lines, BillsBucket bucket) {
		
		String to = member.getTitle() + " " + member.getFirstName() + " " + member.getLastName();
		String billAdress = member.getAddress() + "\n" + member.getZipCode() + " " + member.getCity();
		
		return this.saveAndLockBill(box, nextBillCounter, member, to, billAdress, withStatus, dateAt, payAtDate, paymentMethod, lines, bucket);

	}


	private Bill saveAndLockBill(CrossFitBox box, Long nextBillCounter, Member member, String to, String billAdress, BillStatus withStatus, LocalDate dateAt, LocalDate payAtDate, PaymentMethod paymentMethod, List<BillLine> lines, BillsBucket bucket) {

		Bill bill = new Bill();
		bill.setBox(box);
		bill.setMember(member);
		bill.setStatus(withStatus);
		bill.setPaymentMethod(paymentMethod);
		bill.setEffectiveDate(dateAt);
		bill.setPayAtDate(payAtDate);
		bill.setDisplayName(to);
		bill.setDisplayAddress(billAdress);
		bill.setLines(lines);

		
		lines.forEach(line->line.setTotalTaxIncl(line.getQuantity() * line.getPriceTaxIncl()));
		lines.forEach(line->
			line.setPriceTaxExcl(
					((100 - line.getTaxPerCent())/100) * line.getPriceTaxIncl()
			)
		);
		lines.forEach(line->
			line.setTotalTaxExcl(
				((100 - line.getTaxPerCent())/100) * line.getTotalTaxIncl()
			)
		);
		lines.forEach(line->line.setTotalTax(line.getTotalTaxIncl() - line.getTotalTaxExcl()));		
		bill.setTotalTaxIncl(lines.stream().map(BillLine::getTotalTaxIncl).reduce(Double::sum).orElse(0.0));
		bill.setTotalTaxExcl(lines.stream().map(BillLine::getTotalTaxExcl).reduce(Double::sum).orElse(0.0));
		bill.setTotalTax(bill.getTotalTaxIncl() - bill.getTotalTaxExcl());
		
		lines.forEach(line->line.setBill(bill));
		
		int year = bill.getEffectiveDate().getYear();
		int month = bill.getEffectiveDate().getMonthOfYear();
		Long billsCounter = nextBillCounter == null ? findLastBillCountNumberInYear(year, month, bill.getBox(), bucket)+1 : nextBillCounter;
		
		
		//YYYY-MM-000000
		bill.setNumber(
				StringUtils.leftPad(year+"",  		 4, '0') + "-" + 
				StringUtils.leftPad(month+"", 		 2, '0') + "-" +
				StringUtils.leftPad(billsCounter+"", 6, '0'));
		
		log.trace("Facture de {}€ pour {}: {}", bill.getTotalTaxIncl(), bill.getDisplayName(), bill);
		
		return bucket.save(bill);
	}
	
	private Long findLastBillCountNumberInYear(int year, int month, CrossFitBox box, BillsBucket bucket) {
		String yearStr = year + "";
		
		//YYYY-MM-000000
		String startNumber = yearStr + "%";   
		Page<Bill> billsByNumberDesc = bucket.findAllBillNumberLikeForBoxOrderByNumberDesc(startNumber, box, new PageRequest(0, 1));
		
		Optional<Bill> lastBill = !billsByNumberDesc.hasContent() ? Optional.empty() : Optional.of(billsByNumberDesc.getContent().get(0));
		
		log.trace("Derniere facture en {}: {}", startNumber, lastBill);
		
		Long billsCounter = 0L;
		if (lastBill.isPresent()) {
			String lastBillNumber = lastBill.get().getNumber();
			billsCounter = NumberUtils.toLong(StringUtils.substringAfterLast(lastBillNumber, "-"));
		}
		
		return billsCounter;
	}


	public void deleteDraftBills(CrossFitBox box) {
		billRepository.deleteBillsLine(box, BillStatus.DRAFT);
		billRepository.deleteBills(box, BillStatus.DRAFT);
	}


	public Bill findById(Long id, CrossFitBox box) {
		return billRepository.findOneWithEagerRelation(id, box);
	}

  
}
