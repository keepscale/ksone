package org.crossfit.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST controller for managing Subscription.
 */
@RestController
@RequestMapping("/api")
public class SubscriptionResource {

	private final Logger log = LoggerFactory.getLogger(SubscriptionResource.class);

	@Inject
	private SubscriptionRepository subscriptionRepository;

	/**
	 * POST /subscriptions -> Create a new subscription.
	 */

	@RequestMapping(value = "/subscriptions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
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
	@Timed
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
	@Timed
	public ResponseEntity<List<Subscription>> getAll(@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
		Pageable generatePageRequest = PaginationUtil.generatePageRequest(offset, limit);
		Page<Subscription> page = doFindAll(generatePageRequest);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subscriptions", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	protected Page<Subscription> doFindAll(Pageable generatePageRequest) {
		Page<Subscription> page = subscriptionRepository.findAll(generatePageRequest);
		return page;
	}

	/**
	 * GET /subscriptions/:id -> get the "id" subscription.
	 */

	@RequestMapping(value = "/subscriptions/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Subscription> get(@PathVariable Long id) {
		log.debug("REST request to get Subscription : {}", id);
		return Optional.ofNullable(doGet(id))
				.map(subscription -> new ResponseEntity<>(subscription, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	protected Subscription doGet(Long id) {
		return subscriptionRepository.findOne(id);
	}

	/**
	 * DELETE /subscriptions/:id -> delete the "id" subscription.
	 */

	@RequestMapping(value = "/subscriptions/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete Subscription : {}", id);
		doDelete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("subscription", id.toString())).build();
	}

	protected void doDelete(Long id) {
		subscriptionRepository.delete(id);
	}
}
