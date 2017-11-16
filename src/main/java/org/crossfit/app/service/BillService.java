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
import org.crossfit.app.domain.enumeration.MembershipRulesType;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.repository.BillRepository;
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

	@Autowired
	private BillRepository billRepository;
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	public Page<Bill> findBills(String search, Pageable pageable) {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		
		return billRepository.findAll(box, search, new HashSet<>(Arrays.asList(BillStatus.values())), true, pageable);
	}
	

	public int generateBill(LocalDate since, LocalDate until, int dayOfMonth, BillStatus withStatus, PaymentMethod withPaymentMethod) {

		log.info("Genreation des factures depuis le {} jusqu'au {} au {} de chaque mois avec le statut {} et le paiement {}", 
				since, until, dayOfMonth, withStatus, withPaymentMethod);

		CrossFitBox box = boxService.findCurrentCrossFitBox();
		
		int counter = 0;
		since = since.withDayOfMonth(dayOfMonth);
		while (since.isBefore(until)) {
			Long nextBillCounter = findLastBillCountNumberInYear(since.getYear(), since.getMonthOfYear(), box) + 1;
			
			counter = counter + generateBill(since, withStatus, withPaymentMethod, box, nextBillCounter);
			since = since.plusMonths(1);
		}
		return counter;
	}
	private int generateBill(LocalDate dateAt, BillStatus withStatus, PaymentMethod withPaymentMethod,  CrossFitBox box, Long nextBillCounter) {

		log.info("Genreation des factures au {} avec le statut {}", dateAt, withStatus);
		
		Set<Subscription> subscriptionToBill = subscriptionRepository.findAllByBoxAtDate(box, dateAt);
		
		Map<Member, List<Subscription>> subscriptionByMember = subscriptionToBill.stream().collect(Collectors.groupingBy(Subscription::getMember));
		
		
		int counter = 0;
		for (Member m : subscriptionByMember.keySet()) {
			
			
			List<BillLine> lines = new ArrayList<>();
			
			List<Subscription> subs = subscriptionByMember.get(m);
			for (Subscription sub : subs) {
				//Souscription au mois ?
				if (membershipService.isMembershipPaymentByMonth(sub.getMembership())) {
					BillLine line = new BillLine();
					line.setLabel(sub.getMembership().getName());
					line.setQuantity(1.0);
					line.setPriceTaxIncl(NumberUtils.toDouble(sub.getMembership().getPrice()));
					line.setTotalTaxIncl(line.getQuantity() * line.getPriceTaxIncl());
					lines.add(line);
				}
			}
			
			if (!lines.isEmpty()) {				
				saveAndLockBill(box, nextBillCounter, m, withStatus, withPaymentMethod, dateAt, lines);
				nextBillCounter++;
				counter++;
			}
		}
		return counter;
	}


	public Bill saveAndLockBill(CrossFitBox box, Member member, BillStatus status, PaymentMethod paymentMethod,
			LocalDate effectiveDate, List<BillLine> lines) {
		return saveAndLockBill(box, null, member, status, paymentMethod, effectiveDate, lines);
	}
	
	private Bill saveAndLockBill(CrossFitBox box, Long nextBillCounter, Member member, BillStatus withStatus, PaymentMethod withPaymentMethod, LocalDate dateAt, List<BillLine> lines) {
		
		String to = member.getTitle() + " " + member.getFirstName() + " " + member.getLastName();
		String billAdress = member.getAddress() + "\n" + member.getZipCode() + " " + member.getCity();
		
		return this.saveAndLockBill(box, nextBillCounter, member, to, billAdress, withStatus, withPaymentMethod, dateAt, lines);

	}


	private Bill saveAndLockBill(CrossFitBox box, Long nextBillCounter, Member member, String to, String billAdress, BillStatus withStatus, PaymentMethod withPaymentMethod, LocalDate dateAt, List<BillLine> lines) {

		Bill bill = new Bill();
		bill.setBox(box);
		bill.setMember(member);
		bill.setStatus(withStatus);
		bill.setPaymentMethod(withPaymentMethod);
		bill.setEffectiveDate(dateAt);
		bill.setDisplayName(to);
		bill.setDisplayAddress(billAdress);
		bill.setLines(lines);
		bill.setTotalTaxIncl(lines.stream().map(BillLine::getTotalTaxIncl).reduce(Double::sum).orElse(0.0));
		lines.forEach(line->line.setBill(bill));
		
		int year = bill.getEffectiveDate().getYear();
		int month = bill.getEffectiveDate().getMonthOfYear();
		Long billsCounter = nextBillCounter == null ? findLastBillCountNumberInYear(year, month, bill.getBox())+1 : nextBillCounter;
		
		
		//YYYY-MM-000000
		bill.setNumber(
				StringUtils.leftPad(year+"",  		 4, '0') + "-" + 
				StringUtils.leftPad(month+"", 		 2, '0') + "-" +
				StringUtils.leftPad(billsCounter+"", 6, '0'));
		
		log.trace("Facture de {}€ pour {}: {}", bill.getTotalTaxIncl(), bill.getDisplayName(), bill);
		
		return billRepository.save(bill);
	}
	
	private Long findLastBillCountNumberInYear(int year, int month, CrossFitBox box) {
		String yearStr = year + "";
		
		//YYYY-MM-000000
		String startNumber = yearStr + "%";   
		Page<Bill> billsByNumberDesc = billRepository.findAllBillNumberLikeForBoxOrderByNumberDesc(startNumber, box, new PageRequest(0, 1));
		
		Optional<Bill> lastBill = !billsByNumberDesc.hasContent() ? Optional.empty() : Optional.of(billsByNumberDesc.getContent().get(0));
		
		log.trace("Derniere facture en {}: {}", startNumber, lastBill);
		
		Long billsCounter = 0L;
		if (lastBill.isPresent()) {
			String lastBillNumber = lastBill.get().getNumber();
			billsCounter = NumberUtils.toLong(StringUtils.substringAfterLast(lastBillNumber, "-"));
		}
		
		return billsCounter;
	}


	public void deleteDraftBills() {
		billRepository.deleteBillsLine(BillStatus.DRAFT);
		billRepository.deleteBills(BillStatus.DRAFT);
	}


	public Bill findById(Long id) {
		return billRepository.findOneWithEagerRelation(id);
	}

  
}
