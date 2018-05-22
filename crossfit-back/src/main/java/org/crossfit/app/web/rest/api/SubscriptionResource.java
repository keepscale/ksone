package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Subscription.
 */
@RestController
@RequestMapping("/api")
public class SubscriptionResource {

	private final Logger log = LoggerFactory.getLogger(SubscriptionResource.class);

	@Inject
	private SubscriptionRepository subscriptionRepository;
	@Inject
	private BookingRepository bookingRepository;
	@Inject
	private CrossFitBoxSerivce boxService;

	/**
	 * POST /subscriptions -> Create a new subscription.
	 */

	@RequestMapping(value = "/subscriptions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Subscription> create(@Valid @RequestBody Subscription subscription) throws URISyntaxException {
		log.debug("REST request to save Subscription : {}", subscription);
		if (subscription.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new subscription cannot already have an ID").body(null);
		}
		Subscription result = doSave(subscription);
		return ResponseEntity.created(new URI("/api/subscriptions/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("subscription", result.getId().toString())).body(result);
	}

	protected Subscription doSave(Subscription subscription) {
		Subscription result = subscriptionRepository.save(subscription);
		return result;
	}

	/**
	 * PUT /subscriptions -> Updates an existing subscription.
	 */

	@RequestMapping(value = "/subscriptions", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Subscription> update(@Valid @RequestBody Subscription subscription) throws URISyntaxException {
		log.debug("REST request to update Subscription : {}", subscription);
		if (subscription.getId() == null) {
			return create(subscription);
		}
		Subscription result = doSave(subscription);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("subscription", subscription.getId().toString()))
				.body(result);
	}

	/**
	 * GET /subscriptions -> get all the subscriptions.
	 */

	@RequestMapping(value = "/subscriptions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Subscription>> getAll(
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
		
		
		Pageable generatePageRequest = PaginationUtil.generatePageRequest(offset, limit);
		Page<Subscription> page = doFindAll(search, generatePageRequest);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subscriptions", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	protected Page<Subscription> doFindAll(String search, Pageable generatePageRequest) {
		
		search = search == null ? "" :search;
		String customSearch = "%" + search.replaceAll("\\*", "%").toLowerCase() + "%";
		
		Page<Subscription> page = subscriptionRepository.findAllSubscriptionOfMemberLike(boxService.findCurrentCrossFitBox(), customSearch, generatePageRequest);
		
		List<Subscription> list = new ArrayList<>();
		for (Iterator iterator = page.iterator(); iterator.hasNext();) {
			Subscription subscription = (Subscription) iterator.next();
			
			Subscription s = new Subscription();
			s.setId(subscription.getId());
			s.setMember(subscription.getMember());
			s.setSubscriptionStartDate(subscription.getSubscriptionStartDate());
			s.setSubscriptionEndDate(subscription.getSubscriptionEndDate());
			
			Membership m = new Membership();
			m.setId(subscription.getMembership().getId());
			m.setName(subscription.getMembership().getName());
			s.setMembership(m);
			
			list.add(s);
		}
		
		Page<Subscription> pageJson = new PageImpl<>(list);
		
		return pageJson;
	}

	/**
	 * GET /subscriptions/:id -> get the "id" subscription.
	 */

	@RequestMapping(value = "/subscriptions/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Subscription> get(@PathVariable Long id) {
		log.debug("REST request to get Subscription : {}", id);
		return Optional.ofNullable(doGet(id))
				.map(subscription -> new ResponseEntity<>(subscription, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	

	protected Subscription doGet(Long id) {
		return subscriptionRepository.getOne(id);
	}

	/**
	 * DELETE /subscriptions/:id -> delete the "id" subscription.
	 */

	@RequestMapping(value = "/subscriptions/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete Subscription : {}", id);
		doDelete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("subscription", id.toString())).build();
	}

	protected void doDelete(Long id) {
		subscriptionRepository.deleteById(id);
	}
}
