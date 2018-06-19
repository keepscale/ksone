package org.crossfit.app.web.rest.workouts;

import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.workouts.WodResult;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.WodService;
import org.joda.time.DateTime;
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

	

    /**
     * GET  /results/pastresults -> get all past the results.
     */
    @RequestMapping(value = "/results/pastresults",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<WodResult>> getAllPast(
    		@RequestParam(value = "page" , required = false) Integer page) throws URISyntaxException {
    	
    	CrossFitBox box = boxService.findCurrentCrossFitBox();
    	DateTime end = timeService.nowAsDateTime(box).minusMillis(1);
		DateTime start = end.minusMonths(page);
		Set<WodResult> result = wodService.findMyResultsBetween(start, end);

        Comparator<? super WodResult> comparator = (b1,b2) ->{
        	return b2.getDate().compareTo(b1.getDate());
        };
		return new ResponseEntity<>(result.stream().sorted(comparator).collect(Collectors.toSet()), HttpStatus.OK);
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
	public ResponseEntity<Void> saveMyResult(@PathVariable Long wodId, @PathVariable Long resultId){
		wodService.deleteMyResult(wodId, resultId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
