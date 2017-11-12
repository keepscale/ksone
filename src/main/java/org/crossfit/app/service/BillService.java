package org.crossfit.app.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.crossfit.app.repository.BillRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	public void generateBill(LocalDate dateAt, BillStatus withStatus) {

		log.info("Genreation des factures au {} avec le statut {}", dateAt, withStatus);
		
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		List<Subscription> subscriptionToBill = subscriptionRepository.findAllByBoxAtDate(box, dateAt);
		
		Map<Member, List<Subscription>> subscriptionByMember = subscriptionToBill.stream().collect(Collectors.groupingBy(Subscription::getMember));
		
		List<Bill> lastbills = billRepository.findLastBillInYearForBox(String.valueOf(dateAt.getYear())+"%", box);
		
		Optional<Bill> lastBill = lastbills.isEmpty() ? Optional.empty() : Optional.of(lastbills.get(0));
		
		log.debug("Derniere facture en {}: {}", dateAt.getYear(), lastBill);
		
		Long billsCounter = NumberUtils.toLong(StringUtils.substring(lastBill.map(b->b.getNumber()).orElse(dateAt.getYear() + "-000000"), 5));
		List<MembershipRulesType> typeBillByMonth = Arrays.asList(MembershipRulesType.SUM_PER_4_WEEKS, MembershipRulesType.SUM_PER_MONTH, MembershipRulesType.SUM_PER_WEEK);
		
		List<Bill> bills = new ArrayList<>();
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

				billsCounter++;
				
				Bill bill = new Bill();
				bill.setBox(box);
				bill.setMember(m);
				bill.setStatus(withStatus);
				bill.setEffectiveDate(dateAt);
				bill.setNumber(dateAt.getYear() + "-" + StringUtils.leftPad(billsCounter.toString(), 6, '0'));
				bill.setDisplayName(m.getTitle() + " " + m.getFirstName() + " " + m.getLastName());
				bill.setDisplayAddress(m.getAddress() + "\n" + m.getZipCode() + " " + m.getCity());
				bill.setLines(lines);
				bill.setTotalTaxIncl(lines.stream().map(BillLine::getTotalTaxIncl).reduce(Double::sum).orElse(0.0));
				bills.add(bill);
				lines.forEach(line->line.setBill(bill));
				
				log.debug("Facture de {}€ pour {}: {}", bill.getTotalTaxIncl(), bill.getDisplayName(), bill);
				
			}
		}
		
		billRepository.save(bills);
	}
  
}
