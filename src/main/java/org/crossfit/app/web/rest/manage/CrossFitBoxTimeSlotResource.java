package org.crossfit.app.web.rest.manage;

import javax.inject.Inject;

import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.exception.BadRequestException;
import org.crossfit.app.web.rest.api.TimeSlotResource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing imeSlot.
 */
@RestController
@RequestMapping("/manage")
public class CrossFitBoxTimeSlotResource extends TimeSlotResource {

	@Inject
	private CrossFitBoxSerivce boxService;
	@Inject
	private TimeSlotRepository timeSlotRepository;

	@Override
	protected TimeSlot doSave(TimeSlot timeSlot) throws BadRequestException {
		timeSlot.setBox(boxService.findCurrentCrossFitBox());
		return super.doSave(timeSlot);
	}

	@Override
	protected Page<TimeSlot> doFindAll(Integer offset, Integer limit) {
		return super.doFindAll(offset, limit); // TODO: Filtrer par box
	}

	@Override
	protected TimeSlot doGet(Long id) {
		return super.doGet(id); // TODO: Filtrer par box
	}

	@Override
	protected void doDelete(Long id) {
		TimeSlot timeSlot = timeSlotRepository.findOne(id);
		if (timeSlot.getBox().equals(boxService.findCurrentCrossFitBox())) {
			timeSlotRepository.delete(timeSlot);
		}
	}

}
