package org.crossfit.app.web.rest.workouts;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.workouts.Equipment;
import org.crossfit.app.domain.workouts.Movement;
import org.crossfit.app.domain.workouts.Wod;
import org.crossfit.app.domain.workouts.WodPublication;
import org.crossfit.app.domain.workouts.WodResult;
import org.crossfit.app.domain.workouts.enumeration.WodCategory;
import org.crossfit.app.domain.workouts.enumeration.WodScore;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.WodService;
import org.crossfit.app.web.rest.dto.WodDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.workouts.dto.WodResultCompute;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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
public class WodResource {

	@Autowired
	private WodService wodService;
	@Autowired
	private TimeService timeService;
	@Autowired
    private CrossFitBoxSerivce boxService;
	
	private final Logger log = LoggerFactory.getLogger(WodResource.class);

/*
	@RequestMapping(value = "/wod/{datestr}/withMyResult", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<WodDTO>> getWodAtDateWithMyResult(@PathVariable String datestr){
		
		LocalDate date = LocalDate.parse(datestr);
		if (date == null) {
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Set<Wod> wodsAtDate = wodService.findWodsBetween(date, date);
		Set<WodResult> myResultsAtDate = wodService.findMyResultsAtDate(date);
		
		
		List<WodDTO> results = wodsAtDate.stream()
				.map(wod->{
					return WodDTO.publicMapper(date, 
							myResultsAtDate.stream().filter(r->r.getWod().equals(wod)).findFirst().orElse(null))
							.apply(wod);
				})
				.sorted(Comparator.comparing(WodDTO::getName))
				.collect(Collectors.toList());
		
		return new ResponseEntity<>(results, HttpStatus.OK);
	}
	*/
	/**
	 * GET /wod -> get all the wod.
	 */
	@RequestMapping(value = "/wod", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Wod>> getWods(
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "start", required = false) String startStr,
			@RequestParam(value = "end", required = false) String endStr){

		CrossFitBox box = boxService.findCurrentCrossFitBox();
		
		search = search == null ? "" :search;
		String customSearch = "%" + search.replaceAll("\\*", "%").toLowerCase() + "%";

		LocalDate start = LocalDate.parse(startStr);
		LocalDate end = LocalDate.parse(endStr);
		
		LocalDate nowAsLocalDate = timeService.nowAsLocalDate(box);
		
		Function<Wod, LocalDate> plusPetiteDateApresMaintenant = wod->{
			return wod == null ||wod.getPublications() == null || wod.getPublications().isEmpty() ? null : 
				wod.getPublications().stream()
				.map(WodPublication::getDate)
				.filter(d->d.isAfter(nowAsLocalDate.minusDays(1)))
				.min(LocalDate::compareTo).orElse(null);
		};
		List<Wod> result = wodService.findAllVisibleWod(customSearch, start, end).stream().sorted(
				Comparator.comparing(plusPetiteDateApresMaintenant, Comparator.nullsLast(Comparator.naturalOrder())))
				.collect(Collectors.toList());
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	

	@RequestMapping(value = "/wod/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Wod> getWod(@PathVariable Long id){
		
		Wod result = wodService.findOne(id);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	


	@RequestMapping(value = "/wod/{wodId}/ranking", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<WodResultCompute>> getRanking(@PathVariable Long wodId){
		
		Wod wod = wodService.findOne(wodId);
		Set<WodResult> results = wodService.findAllResult(wod);
		
		List<WodResultCompute> rankings = results.stream()
			.collect(Collectors.toMap(WodResult::getMember, Function.identity(), (r1,r2)->wod.getScore().getComparator().compare(r1, r2) > 0 ? r1 : r2 ))
			.values().stream()
			.sorted(wod.getScore().getComparator())
			.map(result->{
				WodResultCompute compute = new WodResultCompute();
				compute.setId(result.getId());
				compute.setMemberId(result.getMember().getId());
				String displayName = result.getMember().getNickName();
				if (StringUtils.isBlank(displayName)) {
					displayName = result.getMember().getFirstName() + " " + result.getMember().getLastName();
				}
				compute.setDisplayName(displayName);
				compute.setDate(result.getDate());
				compute.setDisplayResult(wod.getScore().getResultMapper().apply(result));
				compute.setCategory(result.getCategory());
				return compute;
			})
			.collect(Collectors.toList());
		
		return new ResponseEntity<>(rankings, HttpStatus.OK);
	}


	/**
	 * POST /wod -> Create a new wod.
	 */
	@RequestMapping(value = "/wod", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Wod> create(@Valid @RequestBody Wod wod) throws URISyntaxException {
		log.debug("REST request to save Wod : {}", wod);
		if (wod.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new wod cannot already have an ID").body(null);
		}
		Wod result = wodService.save(wod);
		return ResponseEntity.created(new URI("/api/wod/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("wod", result.getId().toString())).body(result);
	}

	
	/**
	 * PUT /wod -> Updates an existing wod.
	 */

	@RequestMapping(value = "/wod", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Wod> update(@Valid @RequestBody Wod wod) throws URISyntaxException {
		log.debug("REST request to update Wod : {}", wod);
		if (wod.getId() == null) {
			return create(wod);
		}
		Wod result = wodService.save(wod);
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
