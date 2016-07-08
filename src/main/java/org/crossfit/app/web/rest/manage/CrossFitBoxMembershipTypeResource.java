package org.crossfit.app.web.rest.manage;

import java.util.List;

import javax.inject.Inject;

import org.crossfit.app.domain.MembershipType;
import org.crossfit.app.repository.MembershipTypeRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.api.MembershipTypeResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing MembershipType.
 */
@RestController
@RequestMapping("/manage")
public class CrossFitBoxMembershipTypeResource extends MembershipTypeResource  {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxMembershipTypeResource.class);

    @Inject
    private MembershipTypeRepository membershipTypeRepository;

    @Inject
    private CrossFitBoxSerivce boxService;

	@Override
	protected MembershipType doSave(MembershipType membershipType) {
        membershipType.setBox(boxService.findCurrentCrossFitBox());
		return super.doSave(membershipType);
	}

	@Override
	protected List<MembershipType> doFindAll() {
		return membershipTypeRepository.findAll(boxService.findCurrentCrossFitBox());
	}

	@Override
	protected MembershipType doGet(Long id) {
		return membershipTypeRepository.findOne(id, boxService.findCurrentCrossFitBox());
	}

	@Override
	protected void doDelete(Long id) {
		membershipTypeRepository.delete(id, boxService.findCurrentCrossFitBox());
	}

    
    
}
