package org.crossfit.app.web.rest.admin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.workouts.Wod;
import org.crossfit.app.domain.workouts.enumeration.WodCategory;
import org.crossfit.app.domain.workouts.enumeration.WodScore;
import org.crossfit.app.service.WodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.opencsv.CSVReader;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/admin/wod")
public class ImportWodResource {

	
	private static final String WOD_CSV_FILE_PATH = "classpath:datas/wod.csv";

	@Inject
	private WodService wodService;

	@Inject
	private ApplicationContext appContext;

	private final Logger log = LoggerFactory.getLogger(ImportWodResource.class);


    /**
     * POST  /admin/wod/import -> Import des wods issu du fichier de référence
     * @throws IOException 
     */
    @RequestMapping(value = "/import",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> importWod() throws IOException {
    	
        log.debug("REST request to import wod");

		try(CSVReader csv = new CSVReader(new InputStreamReader(appContext.getResource(WOD_CSV_FILE_PATH).getInputStream()), ';', '"');){
        	List<Wod> wodsToCreate = csv.readAll().stream().map(line->{
        		Wod wod = new Wod();
        		wod.setName(line[0]);
        		wod.setCategory(WodCategory.valueOf(line[1]));
        		wod.setScore(WodScore.valueOf(line[2]));
        		wod.setDescription(line[3]);
        		return wod;
        	}).collect(Collectors.toList());
        	
        	log.debug("Wods: {}", wodsToCreate);
        	
        	this.wodService.createAll(wodsToCreate);
        }        
    	
		return ResponseEntity.ok().build();
    }	
    
}
