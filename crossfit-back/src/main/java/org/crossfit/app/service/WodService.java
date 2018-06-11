package org.crossfit.app.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.workouts.Equipment;
import org.crossfit.app.domain.workouts.Movement;
import org.crossfit.app.domain.workouts.Wod;
import org.crossfit.app.domain.workouts.WodResult;
import org.crossfit.app.repository.EquipmentRepository;
import org.crossfit.app.repository.MovementRepository;
import org.crossfit.app.repository.WodRepository;
import org.crossfit.app.repository.WodResultRepository;
import org.crossfit.app.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class WodService {

    private final Logger log = LoggerFactory.getLogger(WodService.class);


    @Inject
    private TimeService timeService;
    
    @Inject
    private CrossFitBoxSerivce boxService;

	@Autowired
	private MovementRepository movementRepository;
	@Autowired
	private EquipmentRepository equipmentRepository;
	@Autowired
	private WodRepository wodRepository;
	@Autowired
	private WodResultRepository wodResultRepository;

	
	public List<Equipment> findAllEquipment(String search){
		String query = StringUtils.isEmpty(search) ? "%" : search + "%";
		return this.equipmentRepository.findAll(query);
	}

	
	public List<Movement> findAllMovement(String search){
		String query = StringUtils.isEmpty(search) ? "%" : search + "%";
		return this.movementRepository.findAll(query);
	}


	public Set<Wod> findAll(String search) {
		return wodRepository.findAll(boxService.findCurrentCrossFitBox(), search);
	}

	public Wod save(@Valid Wod dto) {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		Wod wod;
		if (dto.getId() == null){			
			wod = new Wod();
			wod.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
		}
		else{			
			wod= wodRepository.findOne(currentCrossFitBox, dto.getId());
		}
		
		wod.setBox(currentCrossFitBox);
		wod.setCategory(dto.getCategory());
		wod.setDescription(dto.getDescription());
		wod.setLink(dto.getLink());
		wod.setVideoLink(dto.getVideoLink());
		wod.setName(dto.getName());
		wod.setScore(dto.getScore());
		if (dto.getTaggedEquipments() != null) {
			wod.setTaggedEquipments(new HashSet<>(equipmentRepository.findAllById(
					dto.getTaggedEquipments().stream().map(Equipment::getId).collect(Collectors.toList()
			))));
		}
		else {
			wod.setTaggedEquipments(null);
		}
		

		if (dto.getTaggedMovements() != null) {
			wod.setTaggedMovements(new HashSet<>(movementRepository.findAllById(
					dto.getTaggedMovements().stream().map(Movement::getId).collect(Collectors.toList()
			))));
		}
		else {
			wod.setTaggedMovements(null);
		}
		
		if (dto.getPublications() != null) {
			wod.getPublications().removeIf(actual->!dto.getPublications().contains(actual));
			wod.getPublications().addAll(dto.getPublications());
			wod.getPublications().forEach(pub->pub.setWod(wod));
		}
		else {
			wod.setPublications(null);
		}
			
		return wodRepository.saveAndFlush(wod);
	}


	public Wod findOne(Long id) {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		return wodRepository.findOne(currentCrossFitBox, id);
	}


	public Set<WodResult> findMyResults(Long wodId) {

		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		
		return wodResultRepository.findAll(currentCrossFitBox, wodId, SecurityUtils.getCurrentMember());
	}


	public Set<WodResult> saveMyResults(Long wodId, @Valid List<WodResult> resultsDto) {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		Wod wod = wodRepository.findOne(currentCrossFitBox, wodId);
		
		Set<WodResult> myActualresult = findMyResults(wodId);

		List<WodResult> resultsToDelete = myActualresult.stream().filter(r->!resultsDto.contains(r)).collect(Collectors.toList());
		List<WodResult> resultsToPersist = new ArrayList<>();
		for (WodResult resultDto : resultsDto) {
			WodResult result = new WodResult();
			if (resultDto.getId() != null) {
				result = wodResultRepository.findOne(resultDto.getId(), wod, SecurityUtils.getCurrentMember());
				if (result == null) {
					continue;
				}
			}
			mergeResult(wod, result, resultDto);
			resultsToPersist.add(result);
		}
		
		wodResultRepository.deleteAll(resultsToDelete);
		wodResultRepository.saveAll(resultsToPersist);
		
		return new HashSet<>(resultsToPersist);
	}


	private void mergeResult(Wod wod, WodResult result, WodResult dto) {
		result.setDate(dto.getDate());
		result.setWod(wod);
		result.setMember(SecurityUtils.getCurrentMember());
		switch (wod.getScore()) {
		case FOR_LOAD:
			result.setTotalLoadInKilo(result.getTotalLoadInKilo());
			result.setTotalMinute(null);
			result.setTotalSecond(null);
			result.setTotalCompleteRound(null);
			result.setTotalReps(null);
			break;
		case FOR_ROUNDS_REPS:
			result.setTotalLoadInKilo(null);
			result.setTotalMinute(null);
			result.setTotalSecond(null);
			result.setTotalCompleteRound(dto.getTotalCompleteRound());
			result.setTotalReps(dto.getTotalReps());
			break;
		case FOR_TIME:
			result.setTotalLoadInKilo(null);
			result.setTotalMinute(dto.getTotalMinute());
			result.setTotalSecond(dto.getTotalSecond());
			result.setTotalCompleteRound(null);
			result.setTotalReps(null);
			break;
			
		}
	}
	
}
