package org.crossfit.app.web.rest.admin;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

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
public class AdminSQLResource {

	private final Logger log = LoggerFactory.getLogger(AdminSQLResource.class);

	@Inject
	private EntityManager entityManager;

    /**
     * POST  /query -> executeQuery
     */
    @RequestMapping(value = "/query/select",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List> select(@RequestBody(required=true) String queryStr) {
        log.debug("REST request to query database : {}", queryStr);

        Query query = entityManager.createNativeQuery(queryStr);
        List resultList = query.getResultList();
        
        return ResponseEntity.ok(resultList);
    }	
    

    /**
     * POST  /query -> executeQuery
     */
    @RequestMapping(value = "/query/update",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Integer> updateOrInsertOrDelete(@RequestBody(required=true) String queryStr) {
        log.debug("REST request to update database : {}", queryStr);

        Query query = entityManager.createNativeQuery(queryStr);
        int count = query.executeUpdate();
        return ResponseEntity.ok(count);
    }	
}
