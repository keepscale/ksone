package org.crossfit.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crossfit.app.domain.MembershipType;
import org.crossfit.app.repository.MembershipTypeRepository;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing MembershipType.
 */
@RestController
@RequestMapping("/api")
public class MembershipTypeResource {

    private final Logger log = LoggerFactory.getLogger(MembershipTypeResource.class);

    @Inject
    private MembershipTypeRepository membershipTypeRepository;

    /**
     * POST  /membershipTypes -> Create a new membershipType.
     */
    @RequestMapping(value = "/membershipTypes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MembershipType> create(@Valid @RequestBody MembershipType membershipType) throws URISyntaxException {
        log.debug("REST request to save MembershipType : {}", membershipType);
        if (membershipType.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new membershipType cannot already have an ID").body(null);
        }
        MembershipType result = doSave(membershipType);
        return ResponseEntity.created(new URI("/api/membershipTypes/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("membershipType", result.getId().toString()))
                .body(result);
    }

	protected MembershipType doSave(MembershipType membershipType) {
		MembershipType result = membershipTypeRepository.save(membershipType);
		return result;
	}

    /**
     * PUT  /membershipTypes -> Updates an existing membershipType.
     */
    @RequestMapping(value = "/membershipTypes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MembershipType> update(@Valid @RequestBody MembershipType membershipType) throws URISyntaxException {
        log.debug("REST request to update MembershipType : {}", membershipType);
        if (membershipType.getId() == null) {
            return create(membershipType);
        }
        MembershipType result = doSave(membershipType);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("membershipType", membershipType.getId().toString()))
                .body(result);
    }

    /**
     * GET  /membershipTypes -> get all the membershipTypes.
     */
    @RequestMapping(value = "/membershipTypes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<MembershipType> getAll() {
        log.debug("REST request to get all MembershipTypes");
        return doFindAll();
    }

	protected List<MembershipType> doFindAll() {
		return membershipTypeRepository.findAll();
	}

    /**
     * GET  /membershipTypes/:id -> get the "id" membershipType.
     */
    @RequestMapping(value = "/membershipTypes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MembershipType> get(@PathVariable Long id) {
        log.debug("REST request to get MembershipType : {}", id);
        return Optional.ofNullable(doGet(id))
            .map(membershipType -> new ResponseEntity<>(
                membershipType,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

	protected MembershipType doGet(Long id) {
		return membershipTypeRepository.findOne(id);
	}

    /**
     * DELETE  /membershipTypes/:id -> delete the "id" membershipType.
     */
    @RequestMapping(value = "/membershipTypes/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete MembershipType : {}", id);
        doDelete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("membershipType", id.toString())).build();
    }

	protected void doDelete(Long id) {
		membershipTypeRepository.delete(id);
	}
}
