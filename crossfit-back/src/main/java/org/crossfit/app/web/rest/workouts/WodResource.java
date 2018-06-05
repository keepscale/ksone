package org.crossfit.app.web.rest.workouts;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.crossfit.app.domain.workouts.Equipment;
import org.crossfit.app.domain.workouts.Movement;
import org.crossfit.app.domain.workouts.WOD;
import org.crossfit.app.domain.workouts.enumeration.WodCategory;
import org.crossfit.app.domain.workouts.enumeration.WodScore;
import org.crossfit.app.service.WodService;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class WodResource {

	@Autowired
	private WodService wodService;

	private final Logger log = LoggerFactory.getLogger(WodResource.class);

	/**
	 * GET /wod -> get all the wod.
	 */
	@RequestMapping(value = "/wod", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<WOD>> getWods(
			@RequestParam(value = "search", required = false) String search){
		List<WOD> result = wodService.findAll(search);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * POST /wod -> Create a new wod.
	 */
	@RequestMapping(value = "/wod", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WOD> create(@Valid @RequestBody WOD wod) throws URISyntaxException {
		log.debug("REST request to save Wod : {}", wod);
		if (wod.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new wod cannot already have an ID").body(null);
		}
		WOD result = wodService.save(wod);
		return ResponseEntity.created(new URI("/api/wod/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("wod", result.getId().toString())).body(result);
	}

	
	/**
	 * PUT /wod -> Updates an existing wod.
	 */

	@RequestMapping(value = "/wod", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<WOD> update(@Valid @RequestBody WOD wod) throws URISyntaxException {
		log.debug("REST request to update Wod : {}", wod);
		if (wod.getId() == null) {
			return create(wod);
		}
		WOD result = wodService.save(wod);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(" wod", result.getId().toString()))
				.body(result);
	}
	
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
