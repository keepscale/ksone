package org.crossfit.app.service;

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
import org.crossfit.app.domain.workouts.WodShareProperties;
import org.crossfit.app.domain.workouts.enumeration.WodVisibility;
import org.crossfit.app.repository.EquipmentRepository;
import org.crossfit.app.repository.MovementRepository;
import org.crossfit.app.repository.WodRepository;
import org.crossfit.app.repository.WodResultRepository;
import org.crossfit.app.security.SecurityUtils;
import org.joda.time.LocalDate;
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


	public Set<Wod> findAllMyWod(String search) {
		return wodRepository.findAll(boxService.findCurrentCrossFitBox(), SecurityUtils.getCurrentMember(), search);
	}

	public Wod save(@Valid Wod dto) {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		Wod wod;
		if (dto.getId() == null){			
			wod = new Wod();
			wod.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
			WodShareProperties shareProperties = new WodShareProperties();
			shareProperties.setOwner(SecurityUtils.getCurrentMember());
			shareProperties.setVisibility(WodVisibility.PUBLIC);
			wod.setShareProperties(shareProperties);
		}
		else{			
			wod= wodRepository.findOne(currentCrossFitBox, SecurityUtils.getCurrentMember(), dto.getId());
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
			wod.getPublications().clear();
		}
			
		return wodRepository.saveAndFlush(wod);
	}


	public Wod findOne(Long id) {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		return wodRepository.findOne(currentCrossFitBox, SecurityUtils.getCurrentMember(), id);
	}


	public Set<WodResult> findMyResults(Long wodId) {

		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		
		return wodResultRepository.findAll(currentCrossFitBox, wodId, SecurityUtils.getCurrentMember());
	}


	public Set<WodResult> findAllResult(Wod wod, LocalDate date) {
		return wodResultRepository.findAll(wod, date);
	}


	public void deleteMyResult(Long wodId, Long resultId) {
		Wod wod = findOne(wodId);
		WodResult result = wodResultRepository.findOne(resultId, wod, SecurityUtils.getCurrentMember());
		if (result != null) {
			wodResultRepository.delete(result);
		}
	}

	public WodResult saveMyResult(Long wodId, @Valid WodResult resultDto) {
		Wod wod = findOne(wodId);
		WodResult result = new WodResult();
		
		if (resultDto.getId() != null) {
			result = wodResultRepository.findOne(resultDto.getId(), wod, SecurityUtils.getCurrentMember());
			if (result == null) {
				return null;
			}
		}
		mergeResult(wod, result, resultDto);
		return 	wodResultRepository.save(result);
	}
	


	private void mergeResult(Wod wod, WodResult result, WodResult dto) {
		result.setDate(dto.getDate());
		result.setWod(wod);
		result.setMember(SecurityUtils.getCurrentMember());
		result.setCategory(dto.getCategory());
		result.setTitle(dto.getTitle());
		switch (wod.getScore()) {
		case FOR_LOAD:
			result.setTotalLoadInKilo(getOrDefault(dto.getTotalLoadInKilo(), 0.0));
			result.setTotalMinute(null);
			result.setTotalSecond(null);
			result.setTotalCompleteRound(null);
			result.setTotalReps(null);
			break;
		case FOR_ROUNDS_REPS:
			result.setTotalLoadInKilo(null);
			result.setTotalMinute(null);
			result.setTotalSecond(null);
			result.setTotalCompleteRound(getOrDefault(dto.getTotalCompleteRound(), 0));
			result.setTotalReps(getOrDefault(dto.getTotalReps(), 0));
			break;
		case FOR_TIME:
			result.setTotalLoadInKilo(null);
			result.setTotalMinute(getOrDefault(dto.getTotalMinute(), 0));
			result.setTotalSecond(getOrDefault(dto.getTotalSecond(), 0));
			result.setTotalCompleteRound(null);
			result.setTotalReps(null);
			break;
			
		}
	}

	private static Integer getOrDefault(Integer value, Integer defaultValue) {
		return value == null ? defaultValue : value;
	}
	private static Double getOrDefault(Double value, Double defaultValue) {
		return value == null ? defaultValue : value;
	}

	public Set<Wod> findWodsBetween(LocalDate start, LocalDate end) {
		CrossFitBox box = boxService.findCurrentCrossFitBox();		
		return this.wodRepository.findAll(box, SecurityUtils.getCurrentMember(), start, end);
	}


	public Set<WodResult> findMyResultsBetween(LocalDate start, LocalDate end) {
		CrossFitBox box = boxService.findCurrentCrossFitBox();		
		return this.wodResultRepository.findAll(box, SecurityUtils.getCurrentMember(), start, end);
	}
}
