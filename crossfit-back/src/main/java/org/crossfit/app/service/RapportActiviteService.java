package org.crossfit.app.service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.repository.CrossFitBoxRepository;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class RapportActiviteService {


	private final Logger log = LoggerFactory.getLogger(RapportActiviteService.class);

	@Autowired
	private CrossFitBoxRepository crossFitBoxRepository;
	@Autowired
	private MembershipRepository membershipRepository;
	@Autowired
	private MemberService memberService;
	@Autowired
	private MailService mailService;
	

    @Scheduled(cron = "0 0 0 * * ?")
    public void sendRapportActivite() {
        LocalDate now = LocalDate.now();
        
        for (CrossFitBox box : crossFitBoxRepository.findAll()) {
            
            List<Integer> daysToNotify = box.getSendRapportActiviteAtDays();
            
            if (daysToNotify.contains(now.dayOfWeek().get())) {
            	
                List<String> to = getSendTo(box);
             
                forceSendRapportActivite(box, to);
				
            }
            else {
            	log.info("Envoi du rapport uniquement les jours suivants: {}", daysToNotify);
            }
        	
		}
        
    }

	public void forceSendRapportActivite(CrossFitBox box, List<String> to) {
		
        int forNextXDays = box.getSendRapportActiviteForNextXDay();

        log.info("Envoi du rapport d'activite des {} prochains jours de {} à {}", forNextXDays, box.getName(), to);
        
        LocalDate now = LocalDate.now();
        
		Set<Long> membershipsIds = membershipRepository.findAllWithRules(box).stream().filter(MembershipService::isMembershipPaymentByMonth).map(Membership::getId).collect(Collectors.toSet());
		List<Member> memberWithSubscriptionNow = memberService.findAllMemberWithActiveSubscriptionAtDate(now.minusDays(1), membershipsIds, false);
		List<Member> memberWithNoSubscriptionInXDays = memberService.findAllMemberWithNoActiveSubscriptionAtDate(now.plusDays(forNextXDays), membershipsIds, false);
		
		Set<Member> membersConcerned = memberWithSubscriptionNow.stream()
		    .distinct()
		    .filter(memberWithNoSubscriptionInXDays::contains)
		    .sorted(Comparator.comparing(Member::getFirstName))
		    .collect(Collectors.toCollection(LinkedHashSet::new));
		
		mailService.sendRapportActivite(box, to, membersConcerned);
	}

	private List<String> getSendTo(CrossFitBox box) {
		return memberService.findAllMemberWithAuthorities(box, AuthoritiesConstants.DIRECTOR, AuthoritiesConstants.ADMIN).stream().map(Member::getLogin).distinct().collect(Collectors.toList());
	}
    
    
}
