package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MembershipService;
import org.crossfit.app.web.rest.dto.MembershipDTO;
import org.crossfit.app.web.rest.errors.CustomParameterizedException;
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
public class MembershipResource {

	private final Logger log = LoggerFactory.getLogger(MembershipResource.class);

	@Inject
	private MembershipRepository membershipRepository;

    @Inject
    private CrossFitBoxSerivce boxService;
    @Inject
    private MembershipService membershipService;
    @Inject
    private SubscriptionRepository subscriptionRepository;

	/**
	 * POST /memberships -> Create a new membershipType.
	 */
	@RequestMapping(value = "/memberships", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MembershipDTO> create(@Valid @RequestBody MembershipDTO membershipType)
			throws URISyntaxException {
		log.debug("REST request to save MembershipType : {}", membershipType);
		if (membershipType.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new membershipType cannot already have an ID")
					.body(null);
		}
		MembershipDTO result = convert(true).apply(membershipService.doSave(membershipType));
		return ResponseEntity.created(new URI("/api/memberships/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("membershipType", result.getId().toString()))
				.body(result);
	}

	/**
	 * PUT /memberships -> Updates an existing membershipType.
	 */
	@RequestMapping(value = "/memberships", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MembershipDTO> update(@Valid @RequestBody MembershipDTO membershipType)
			throws URISyntaxException {
		log.debug("REST request to update MembershipType : {}", membershipType);
		if (membershipType.getId() == null) {
			return create(membershipType);
		}
		MembershipDTO result = convert(true).apply(membershipService.doSave(membershipType));
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("membershipType", membershipType.getId().toString()))
				.body(result);
	}

	/**
	 * GET /memberships -> get all the memberships.
	 */
	@RequestMapping(value = "/memberships", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<MembershipDTO> getAll() {
		log.debug("REST request to get all memberships");
		return doFindAll();
	}
	

	protected List<MembershipDTO> doFindAll() {
		return membershipRepository.findAll(boxService.findCurrentCrossFitBox())
				.stream().map(convert(false)).collect(Collectors.toList());
	}

	public static final Function<Membership, MembershipDTO> convert(boolean withrelation) {
		return (membership)->{
			MembershipDTO result = new MembershipDTO();
			result.setId(membership.getId());
			result.setName(membership.getName());
			result.setPriceTaxIncl(membership.getPriceTaxIncl());
			result.setTaxPerCent(membership.getTaxPerCent());
			result.setAddByDefault(membership.isAddByDefault());
			result.setNbMonthValidity(membership.getNbMonthValidity());
			if (withrelation){
				result.setMembershipRules(membership.getMembershipRules());
			}
			return result;
		};
	}

	/**
	 * GET /memberships/:id -> get the "id" membershipType.
	 */
	@RequestMapping(value = "/memberships/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MembershipDTO> get(@PathVariable Long id) {
		log.debug("REST request to get Membership : {}", id);
		return Optional.ofNullable(doGet(id))
				.map(membership -> new ResponseEntity<>(convert(true).apply(membership), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	protected Membership doGet(Long id) {
		return membershipRepository.findOne(id, boxService.findCurrentCrossFitBox());
	}

	/**
	 * DELETE /memberships/:id -> delete the "id" membershipType.
	 */
	@RequestMapping(value = "/memberships/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete MembershipType : {}", id);

		
		Membership membership = doGet(id);
		if (membership.getBox().equals(boxService.findCurrentCrossFitBox())){
			List<Subscription> subscriptions = subscriptionRepository.findAllByMembership(membership);
			if (subscriptions.isEmpty()){
				membershipRepository.deleteById(id);
			}
			else{
	    		throw new CustomParameterizedException("Il existe des adhésions à cet abonnement");
			}
		}
		else{
			ResponseEntity.status(HttpStatus.FORBIDDEN);
		}
		
				
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("membershipType", id.toString()))
				.build();
	}

	protected void doDelete(Long id) {
		membershipRepository.delete(id, boxService.findCurrentCrossFitBox());
	}
}
