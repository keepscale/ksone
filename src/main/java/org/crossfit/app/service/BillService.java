package org.crossfit.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

	@Autowired
	private BillRepository billRepository;
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	public Page<Bill> findBills(String search, Pageable pageable) {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		
		return billRepository.findAll(box, search, new HashSet<>(Arrays.asList(BillStatus.values())), true, pageable);
	}
	
	public void generateBill(LocalDate dateAt, BillStatus withStatus, PaymentMethod withPaymentMethod) {

		log.info("Genreation des factures au {} avec le statut {}", dateAt, withStatus);
		
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		List<Subscription> subscriptionToBill = subscriptionRepository.findAllByBoxAtDate(box, dateAt);
		
		Map<Member, List<Subscription>> subscriptionByMember = subscriptionToBill.stream().collect(Collectors.groupingBy(Subscription::getMember));
		
		List<MembershipRulesType> typeBillByMonth = Arrays.asList(MembershipRulesType.SUM_PER_4_WEEKS, MembershipRulesType.SUM_PER_MONTH, MembershipRulesType.SUM_PER_WEEK);
		
		for (Member m : subscriptionByMember.keySet()) {
			
			
			List<BillLine> lines = new ArrayList<>();
			
			List<Subscription> subs = subscriptionByMember.get(m);
			for (Subscription sub : subs) {
				//Souscription au mois ?
				if (sub.getMembership().getMembershipRules().stream().anyMatch(rule->typeBillByMonth.contains(rule.getType()))) {
					BillLine line = new BillLine();
					line.setLabel(sub.getMembership().getName());
					line.setQuantity(1.0);
					line.setPriceTaxIncl(NumberUtils.toDouble(sub.getMembership().getPrice()));
					line.setTotalTaxIncl(line.getQuantity() * line.getPriceTaxIncl());
					lines.add(line);
				}
			}
			
			if (!lines.isEmpty()) {				
				saveAndLockBill(box, m, withStatus, withPaymentMethod, dateAt, lines);
			}
		}
		
	}
	
	public Bill saveAndLockBill(CrossFitBox box, Member member, BillStatus withStatus, PaymentMethod withPaymentMethod, LocalDate dateAt, List<BillLine> lines) {
		
		String to = member.getTitle() + " " + member.getFirstName() + " " + member.getLastName();
		String billAdress = member.getAddress() + "\n" + member.getZipCode() + " " + member.getCity();
		
		return this.saveAndLockBill(box, member, to, billAdress, withStatus, withPaymentMethod, dateAt, lines);

	}


	private Bill saveAndLockBill(CrossFitBox box, Member member, String to, String billAdress, BillStatus withStatus, PaymentMethod withPaymentMethod, LocalDate dateAt, List<BillLine> lines) {

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
		Long billsCounter = findLastBillCountNumberInYear(year, month, bill.getBox()) + 1;
		
		//YYYY-MM-000000
		bill.setNumber(
				StringUtils.leftPad(year+"",  		 4, '0') + "-" + 
				StringUtils.leftPad(month+"", 		 2, '0') + "-" +
				StringUtils.leftPad(billsCounter+"", 6, '0'));
		
		log.debug("Facture de {}€ pour {}: {}", bill.getTotalTaxIncl(), bill.getDisplayName(), bill);
		
		return billRepository.saveAndFlush(bill);
	}
	
	private Long findLastBillCountNumberInYear(int year, int month, CrossFitBox box) {
		String yearStr = year + "";
		String monthStr = StringUtils.leftPad(month+"", 2, '0');
		
		//YYYY-MM-000000
		String startNumber = yearStr + "%";   
		List<Bill> billsByNumberDesc = billRepository.findAllBillNumberLikeForBoxOrderByNumberDesc(startNumber, box);
		
		Optional<Bill> lastBill = billsByNumberDesc.isEmpty() ? Optional.empty() : Optional.of(billsByNumberDesc.get(0));
		
		log.debug("Derniere facture en {}: {}", startNumber, lastBill);
		
		Long billsCounter = 0L;
		if (lastBill.isPresent()) {
			String lastBillNumber = lastBill.get().getNumber();
			billsCounter = NumberUtils.toLong(StringUtils.substringAfterLast(lastBillNumber, "-"));
		}
		
		return billsCounter;
	}
  
}
