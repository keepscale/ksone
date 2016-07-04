package org.crossfit.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.MemberRepository;
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
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/api")
public class MemberResource {

	private final Logger log = LoggerFactory.getLogger(MemberResource.class);

	@Inject
	private MemberRepository memberRepository;

	/**
	 * POST /members -> Create a new member.
	 */

	@RequestMapping(value = "/members", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Member> create(@Valid @RequestBody Member member) throws URISyntaxException {
		log.debug("REST request to save Member : {}", member);
		if (member.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new member cannot already have an ID").body(null);
		}
		Member result = doSave(member);
		return ResponseEntity.created(new URI("/api/members/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("member", result.getId().toString())).body(result);
	}

	protected Member doSave(Member member) {
		Member result = memberRepository.save(member);
		return result;
	}

	/**
	 * PUT /members -> Updates an existing member.
	 */

	@RequestMapping(value = "/members", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Member> update(@Valid @RequestBody Member member) throws URISyntaxException {
		log.debug("REST request to update Member : {}", member);
		if (member.getId() == null) {
			return create(member);
		}
		Member result = doSave(member);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("member", member.getId().toString()))
				.body(result);
	}
	
	/**
	 * GET /members -> get all the members.
	 */

	@RequestMapping(value = "/members", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Member>> getAll(@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
		Pageable generatePageRequest = PaginationUtil.generatePageRequest(offset, limit);
		Page<Member> page = doFindAll(generatePageRequest);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/members", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	protected Page<Member> doFindAll(Pageable generatePageRequest) {
		Page<Member> page = memberRepository.findAll(generatePageRequest);
		return page;
	}

	/**
	 * GET /members/:id -> get the "id" member.
	 */

	@RequestMapping(value = "/members/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Member> get(@PathVariable Long id) {
		log.debug("REST request to get Member : {}", id);
		return Optional.ofNullable(doGet(id))
				.map(member -> new ResponseEntity<>(member, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	protected Member doGet(Long id) {
		return memberRepository.findOne(id);
	}

	/**
	 * DELETE /members/:id -> delete the "id" member.
	 */

	@RequestMapping(value = "/members/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete Member : {}", id);
		doDelete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("member", id.toString())).build();
	}

	protected void doDelete(Long id) {
		memberRepository.delete(id);
	}
}
