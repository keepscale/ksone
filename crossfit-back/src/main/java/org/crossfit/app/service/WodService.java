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
import org.crossfit.app.repository.EquipmentRepository;
import org.crossfit.app.repository.MovementRepository;
import org.crossfit.app.repository.WodRepository;
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
			
		return wodRepository.saveAndFlush(wod);
	}


	public Wod findOne(Long id) {
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		return wodRepository.findOne(currentCrossFitBox, id);
	}
	
}
