package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.repository.ClosedDayRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing ClosedDay.
 */
@RestController
@RequestMapping("/api")
public class ClosedDayResource {

	private final Logger log = LoggerFactory.getLogger(ClosedDayResource.class);


    @Inject
    private ClosedDayRepository closedDayRepository;
    
    @Inject
    private CrossFitBoxSerivce boxService;

	protected ClosedDay doSave(ClosedDay closedDay) {
		closedDay.setBox(boxService.findCurrentCrossFitBox());
		ClosedDay result = closedDayRepository.save(closedDay);
		return result;	}

	protected List<ClosedDay> doFindAll() {
		return closedDayRepository.findAll(boxService.findCurrentCrossFitBox());
	}

	protected ClosedDay doGet(Long id) {
		return closedDayRepository.findOne(id, boxService.findCurrentCrossFitBox());
	}

	protected void doDelete(Long id) {
		closedDayRepository.delete(id, boxService.findCurrentCrossFitBox());
	}

	/**
	 * POST /closedDays -> Create a new closedDay.
	 */
	@RequestMapping(value = "/closedDays", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClosedDay> create(@Valid @RequestBody ClosedDay closedDay) throws URISyntaxException {
		log.debug("REST request to save ClosedDay : {}", closedDay);
		if (closedDay.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new closedDay cannot already have an ID")
					.body(null);
		}
		ClosedDay result = doSave(closedDay);
		return ResponseEntity.created(new URI("/api/closedDays/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("closedDay", result.getId().toString())).body(result);
	}

	/**
	 * PUT /closedDays -> Updates an existing closedDay.
	 */
	@RequestMapping(value = "/closedDays", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClosedDay> update(@Valid @RequestBody ClosedDay closedDay) throws URISyntaxException {
		log.debug("REST request to update ClosedDay : {}", closedDay);
		if (closedDay.getId() == null) {
			return create(closedDay);
		}
		ClosedDay result = doSave(closedDay);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("closedDay", closedDay.getId().toString())).body(result);
	}

	/**
	 * GET /closedDays -> get all the closedDays.
	 */
	@RequestMapping(value = "/closedDays", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ClosedDay> getAll() {
		log.debug("REST request to get all ClosedDays");
		return doFindAll();
	}

	/**
	 * GET /closedDays/:id -> get the "id" closedDay.
	 */
	@RequestMapping(value = "/closedDays/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClosedDay> get(@PathVariable Long id) {
		log.debug("REST request to get ClosedDay : {}", id);
		return Optional.ofNullable(doGet(id)).map(closedDay -> new ResponseEntity<>(closedDay, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /closedDays/:id -> delete the "id" closedDay.
	 */
	@RequestMapping(value = "/closedDays/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete ClosedDay : {}", id);
		doDelete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("closedDay", id.toString())).build();
	}
}
