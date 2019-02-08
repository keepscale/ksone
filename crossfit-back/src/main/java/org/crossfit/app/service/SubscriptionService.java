package org.crossfit.app.service;

import javax.inject.Inject;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.MembershipRulesType;
import org.crossfit.app.exception.SubscriptionAlreadySignException;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.web.rest.dto.MembershipDTO;
import org.crossfit.app.web.rest.dto.SubscriptionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing subscription.
 */
@Service
@Transactional
public class SubscriptionService {

    private final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    @Inject
    private CrossFitBoxSerivce boxService;

	@Inject
	private SubscriptionRepository subscriptionRepository;


	public Subscription sign(SubscriptionDTO dto) throws SubscriptionAlreadySignException {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		Subscription subscription = subscriptionRepository.getOne(dto.getId());
		
		if (subscription.getSignatureDate() != null) {
			throw new SubscriptionAlreadySignException(subscription);
		}
		
		subscription.setSignatureDate(dto.getSignatureDate());
		subscription.setSignatureDataEncoded(dto.getSignatureDataEncoded());
		
		//TODO: send by mail ?
		
		return subscriptionRepository.save(subscription);
	}
}
