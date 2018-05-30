package org.crossfit.app.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.workouts.Equipment;
import org.crossfit.app.domain.workouts.Movement;
import org.crossfit.app.repository.EquipmentRepository;
import org.crossfit.app.repository.MovementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class WodService {

    private final Logger log = LoggerFactory.getLogger(WodService.class);


	@Autowired
	private MovementRepository movementRepository;
	@Autowired
	private EquipmentRepository equipmentRepository;

	
	public List<Equipment> findAllEquipment(String search){
		String query = StringUtils.isEmpty(search) ? "%" : search + "%";
		return this.equipmentRepository.findAll(query);
	}

	
	public List<Movement> findAllMovement(String search){
		String query = StringUtils.isEmpty(search) ? "%" : search + "%";
		return this.movementRepository.findAll(query);
	}
	
}
