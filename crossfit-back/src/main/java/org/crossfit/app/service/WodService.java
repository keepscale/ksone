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
import org.crossfit.app.domain.workouts.WodShareProperties;
import org.crossfit.app.domain.workouts.enumeration.WodVisibility;
import org.crossfit.app.domain.workouts.result.WodResult;
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


	public Set<Wod> findAllWod(String search, LocalDate start, LocalDate end) {
		log.debug("findAllVisibleWod(search={}, start={}, end={}", search, start, end);
		return wodRepository.findAll(boxService.findCurrentCrossFitBox(), search, start, end);
	}

	public Wod save(@Valid Wod dto) {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		
		Wod wod = dto.getId() == null ? createWod(currentCrossFitBox) 
				: wodRepository.findOne(currentCrossFitBox, SecurityUtils.getCurrentMember(), dto.getId())	;
		
		mergeWod(dto, wod);
			
		return wodRepository.saveAndFlush(wod);
	}


	private void mergeWod(Wod source, Wod destination) {
		destination.setCategory(source.getCategory());
		destination.setDescription(source.getDescription());
		destination.setLink(source.getLink());
		destination.setVideoLink(source.getVideoLink());
		destination.setName(source.getName());
		destination.setScore(source.getScore());
		if (source.getTaggedEquipments() != null) {
			destination.setTaggedEquipments(new HashSet<>(equipmentRepository.findAllById(
					source.getTaggedEquipments().stream().map(Equipment::getId).collect(Collectors.toList()
			))));
		}
		else {
			destination.setTaggedEquipments(null);
		}
		

		if (source.getTaggedMovements() != null) {
			destination.setTaggedMovements(new HashSet<>(movementRepository.findAllById(
					source.getTaggedMovements().stream().map(Movement::getId).collect(Collectors.toList()
			))));
		}
		else {
			destination.setTaggedMovements(null);
		}
		
		if (source.getPublications() != null) {
			destination.getPublications().removeIf(actual->!source.getPublications().contains(actual));
			destination.getPublications().addAll(source.getPublications());
			destination.getPublications().forEach(pub->pub.setWod(destination));
		}
		else {
			destination.getPublications().clear();
		}
	}


	private Wod createWod(CrossFitBox currentCrossFitBox) {
		Wod wod;
		wod = new Wod();
		wod.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
		wod.setBox(currentCrossFitBox);
		WodShareProperties shareProperties = new WodShareProperties();
		shareProperties.setOwner(SecurityUtils.getCurrentMember());
		shareProperties.setVisibility(WodVisibility.PUBLIC);
		wod.setShareProperties(shareProperties);
		return wod;
	}


	public Wod findOne(Long id) {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		return wodRepository.findOne(currentCrossFitBox, SecurityUtils.getCurrentMember(), id);
	}


	public Set<WodResult> findMyResults(Long wodId) {

		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		
		return wodResultRepository.findAll(currentCrossFitBox, wodId, SecurityUtils.getCurrentMember());
	}


	public Set<WodResult> findAllResult(Wod wod) {
		return wodResultRepository.findAll(wod);
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

	public Set<WodResult> findMyResultsAtDate(LocalDate atDate) {
		CrossFitBox box = boxService.findCurrentCrossFitBox();		
		return this.wodResultRepository.findAll(box, SecurityUtils.getCurrentMember(), atDate, atDate);
	}


	public void deleteWodAndResult(Long id) {
		Wod wodToDelete = this.findOne(id);
		if (wodToDelete != null) {
			
			Set<WodResult> resultToDelete = this.findAllResult(wodToDelete);
			this.wodResultRepository.deleteAll(resultToDelete);
			this.wodRepository.delete(wodToDelete);
		}
	}


	public void createAll(List<Wod> dtos) {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		
		List<Wod> wodToCreate = dtos.stream().map(dto->{
			Wod wod = createWod(box);
			mergeWod(dto, wod);
			return wod;
		}).collect(Collectors.toList());
		
		this.wodRepository.saveAll(wodToCreate);
	}
}
