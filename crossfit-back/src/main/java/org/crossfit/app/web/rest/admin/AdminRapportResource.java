package org.crossfit.app.web.rest.admin;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.RapportActiviteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/admin")
public class AdminRapportResource {

	private final Logger log = LoggerFactory.getLogger(AdminRapportResource.class);

	@Inject
	private CrossFitBoxSerivce boxSerivce;
	@Inject
	private RapportActiviteService rapportActiviteService;

    /**
     * POST  /query -> executeQuery
     */
    @RequestMapping(value = "/rapport-activite",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendRapportActivite(@RequestBody(required=true) String to) {
        log.debug("REST request to send rapport activite to : {}", to);

        CrossFitBox box = boxSerivce.findCurrentCrossFitBox();
		rapportActiviteService.forceSendRapportActivite(box, Arrays.asList(to.split(";")));
        
        return ResponseEntity.ok().build();
    }	
    
}
