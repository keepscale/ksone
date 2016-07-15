package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.Membership;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing MembershipType.
 */
@RestController
@RequestMapping("/api")
public class MembershipTypeResource {

	private final Logger log = LoggerFactory.getLogger(MembershipTypeResource.class);

	@Inject
	private MembershipRepository membershipTypeRepository;

    @Inject
    private CrossFitBoxSerivce boxService;

	/**
	 * POST /membershipTypes -> Create a new membershipType.
	 */
	@RequestMapping(value = "/membershipTypes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Membership> create(@Valid @RequestBody Membership membershipType)
			throws URISyntaxException {
		log.debug("REST request to save MembershipType : {}", membershipType);
		if (membershipType.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new membershipType cannot already have an ID")
					.body(null);
		}
		Membership result = doSave(membershipType);
		return ResponseEntity.created(new URI("/api/membershipTypes/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("membershipType", result.getId().toString()))
				.body(result);
	}

	protected Membership doSave(Membership membershipType) {
        membershipType.setBox(boxService.findCurrentCrossFitBox());
		Membership result = membershipTypeRepository.save(membershipType);
		return result;
	}

	/**
	 * PUT /membershipTypes -> Updates an existing membershipType.
	 */
	@RequestMapping(value = "/membershipTypes", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Membership> update(@Valid @RequestBody Membership membershipType)
			throws URISyntaxException {
		log.debug("REST request to update MembershipType : {}", membershipType);
		if (membershipType.getId() == null) {
			return create(membershipType);
		}
		Membership result = doSave(membershipType);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("membershipType", membershipType.getId().toString()))
				.body(result);
	}

	/**
	 * GET /membershipTypes -> get all the membershipTypes.
	 */
	@RequestMapping(value = "/membershipTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Membership> getAll() {
		log.debug("REST request to get all MembershipTypes");
		return doFindAll();
	}

	protected List<Membership> doFindAll() {
		return membershipTypeRepository.findAll(boxService.findCurrentCrossFitBox());
	}

	/**
	 * GET /membershipTypes/:id -> get the "id" membershipType.
	 */
	@RequestMapping(value = "/membershipTypes/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Membership> get(@PathVariable Long id) {
		log.debug("REST request to get MembershipType : {}", id);
		return Optional.ofNullable(doGet(id)).map(membershipType -> new ResponseEntity<>(membershipType, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	protected Membership doGet(Long id) {
		return membershipTypeRepository.findOne(id, boxService.findCurrentCrossFitBox());
	}

	/**
	 * DELETE /membershipTypes/:id -> delete the "id" membershipType.
	 */
	@RequestMapping(value = "/membershipTypes/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete MembershipType : {}", id);
		doDelete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("membershipType", id.toString()))
				.build();
	}

	protected void doDelete(Long id) {
		membershipTypeRepository.delete(id, boxService.findCurrentCrossFitBox());
	}
}
