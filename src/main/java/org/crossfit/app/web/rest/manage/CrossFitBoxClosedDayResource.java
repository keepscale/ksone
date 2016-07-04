package org.crossfit.app.web.rest.manage;

import java.util.List;

import javax.inject.Inject;

import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.repository.ClosedDayRepository;
import org.crossfit.app.repository.MembershipTypeRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.ClosedDayResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/manage")
public class CrossFitBoxClosedDayResource extends ClosedDayResource {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxClosedDayResource.class);

    @Inject
    private ClosedDayRepository closedDayRepository;
    
    @Inject
    private CrossFitBoxSerivce boxService;

	@Override
	protected ClosedDay doSave(ClosedDay closedDay) {
		closedDay.setBox(boxService.findCurrentCrossFitBox());
		return super.doSave(closedDay);
	}

	@Override
	protected List<ClosedDay> doFindAll() {
		return closedDayRepository.findAll(boxService.findCurrentCrossFitBox());
	}

	@Override
	protected ClosedDay doGet(Long id) {
		return closedDayRepository.findOne(id, boxService.findCurrentCrossFitBox());
	}

	@Override
	protected void doDelete(Long id) {
		closedDayRepository.delete(id, boxService.findCurrentCrossFitBox());
	}

    

    
}
