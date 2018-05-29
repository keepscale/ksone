package org.crossfit.app.web.rest.workouts;

import org.crossfit.app.domain.workouts.enumeration.WodCategory;
import org.crossfit.app.domain.workouts.enumeration.WodScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Bill.
 */
@RestController
@RequestMapping("/api")
public class WodResource {


	private final Logger log = LoggerFactory.getLogger(WodResource.class);

	/**
	 * GET /wod/scores -> get all the wod scores.
	 */
	@RequestMapping(value = "/wod/scores", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WodScore[]> getWodScore(){
		return new ResponseEntity<>(WodScore.values(), HttpStatus.OK);
	}
	/**
	 * GET /wod/categories -> get all the wod categories.
	 */
	@RequestMapping(value = "/wod/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WodCategory[]> getWodCategory(){
		return new ResponseEntity<>(WodCategory.values(), HttpStatus.OK);
	}

}
