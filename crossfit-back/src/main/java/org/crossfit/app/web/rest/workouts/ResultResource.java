package org.crossfit.app.web.rest.workouts;

import java.util.Set;

import javax.validation.Valid;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.workouts.result.WodResult;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.WodService;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Bill.
 */
@RestController
@RequestMapping("/api")
public class ResultResource {

	@Autowired
	private WodService wodService;
	@Autowired
	private TimeService timeService;
	@Autowired
    private CrossFitBoxSerivce boxService;
	
	private final Logger log = LoggerFactory.getLogger(ResultResource.class);


	@RequestMapping(value = "/wod/myresults", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<WodResult>> getMyResults(
			@RequestParam(value = "date", required = false) String date){
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		DateTime dateParsed = timeService.parseDate("yyyy-MM-dd", date, box);
		LocalDate atDate = dateParsed == null ? null : dateParsed.toLocalDate();
		
		if (atDate == null) {
			log.info("Unable to parse date {}", date);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Set<WodResult> result = wodService.findMyResultsAtDate(atDate);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/wod/{id}/results", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<WodResult>> getMyWodResults(@PathVariable Long id){
		
		Set<WodResult> result = wodService.findMyResults(id);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/wod/{wodId}/results", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WodResult> saveMyResult(@PathVariable Long wodId, @Valid @RequestBody WodResult resultdto){
		WodResult result = wodService.saveMyResult(wodId, resultdto);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/wod/{wodId}/results/{resultId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> deleteResult(@PathVariable Long wodId, @PathVariable Long resultId){
		wodService.deleteMyResult(wodId, resultId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
