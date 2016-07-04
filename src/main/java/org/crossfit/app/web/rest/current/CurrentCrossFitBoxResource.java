package org.crossfit.app.web.rest.current;

import java.util.List;

import javax.inject.Inject;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing CrossFitBox.
 */
@RestController
@RequestMapping("/public")
public class CurrentCrossFitBoxResource {

	private final Logger log = LoggerFactory
			.getLogger(CurrentCrossFitBoxResource.class);

	@Inject
	private CrossFitBoxSerivce boxService;

	@Inject
	private TimeService timeZoneService;

	/**
	 * GET /currentCrossFitBox -> get the "current" crossFitBox resolve by
	 * hostname.
	 */
	@RequestMapping(value = "/currentCrossFitBox", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CrossFitBox> get() {
		log.debug("REST request to get current CrossFitBox");
		return new ResponseEntity<>(boxService.findCurrentCrossFitBox(),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/timezone", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<String> getTimeZones() {
		log.debug("REST request to get available timezone");
		return timeZoneService.getAvailableTimeZones();
	}
}
