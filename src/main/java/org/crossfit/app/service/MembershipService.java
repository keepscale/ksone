package org.crossfit.app.service;

import java.util.HashSet;

import javax.inject.Inject;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.web.rest.dto.MembershipDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class MembershipService {

    private final Logger log = LoggerFactory.getLogger(MembershipService.class);

    @Inject
    private CrossFitBoxSerivce boxService;

	@Inject
	private MembershipRepository membershipRepository;


	public Membership doSave(MembershipDTO membershipDto) {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		Membership membership = membershipRepository.findOne(membershipDto.getId(), currentCrossFitBox);
		if (membership == null){
			membership = new Membership();
		}
		membership.setName(membershipDto.getName());
		membership.setPrice(membershipDto.getPrice());
        membership.setBox(currentCrossFitBox);
        membership.setAddByDefault(membershipDto.isAddByDefault());
        membership.setNbMonthValidity(membershipDto.getNbMonthValidity());
        membership.getMembershipRules().clear();
        for (MembershipRules r : membershipDto.getMembershipRules()) {
        	r.setMembership(membership);
			membership.getMembershipRules().add(r);
		}
        
        Membership result = membershipRepository.save(membership);
		return result;
	}
}
