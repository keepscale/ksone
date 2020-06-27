package org.crossfit.app.web.rest.workouts;

import java.util.List;

import org.crossfit.app.domain.workouts.Equipment;
import org.crossfit.app.domain.workouts.Movement;
import org.crossfit.app.domain.workouts.enumeration.WodCategory;
import org.crossfit.app.domain.workouts.enumeration.WodScore;
import org.crossfit.app.domain.workouts.result.ResultCategory;
import org.crossfit.app.domain.workouts.result.ResultDivision;
import org.crossfit.app.service.WodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Wod enum.
 */
@RestController
@RequestMapping("/api")
public class WodEnumResource {
	
	@Autowired
	public WodService wodService;	

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
	/**
	 * GET /result/categories -> get all the result categories.
	 */
	@RequestMapping(value = "/wod/result-categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResultCategory[]> getResultCategory(){
		return new ResponseEntity<>(ResultCategory.values(), HttpStatus.OK);
	}
	/**
	 * GET /result/division -> get all the ResultDivision.
	 */
	@RequestMapping(value = "/wod/result-divisions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResultDivision[]> getResultDivision(){
		return new ResponseEntity<>(ResultDivision.values(), HttpStatus.OK);
	}

	/**
	 * GET /wod/equipments -> get all the equipments.
	 */
	@RequestMapping(value = "/wod/equipments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Equipment>> getEquipments(@RequestParam(name="search", required=false) String search){
		
		return new ResponseEntity<>(wodService.findAllEquipment(search), HttpStatus.OK);
	}
	
	/**
	 * GET /wod/movements -> get all the movements.
	 */
	@RequestMapping(value = "/wod/movements", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Movement>> getMovements(@RequestParam(name="search", required=false) String search){		
		return new ResponseEntity<>(wodService.findAllMovement(search), HttpStatus.OK);
	}
}