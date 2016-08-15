package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.repository.AuthorityRepository;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.repository.SearchMemberCriteria;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MailService;
import org.crossfit.app.service.MemberService;
import org.crossfit.app.service.util.RandomUtil;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.crossfit.app.web.rest.dto.MembershipDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/api")
public class MemberResource {

	private final Logger log = LoggerFactory.getLogger(MemberResource.class);
	@Inject
	private CrossFitBoxSerivce boxService;
	@Inject
	private MemberService memberService;
	@Inject
	private MemberRepository memberRepository;
    
    @Inject
    private SubscriptionRepository subscriptionRepository;
    
    
	/**
	 * POST /members -> Create a new member.
	 */
	@RequestMapping(value = "/members", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Member> create(@Valid @RequestBody MemberDTO member) throws URISyntaxException {
		log.debug("REST request to save Member : {}", member);
		if (member.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new member cannot already have an ID").body(null);
		}
		Member result = memberService.doSave(member);
		return ResponseEntity.created(new URI("/api/members/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("member", result.getId().toString())).body(result);
	}

	
	/**
	 * PUT /members -> Updates an existing member.
	 */

	@RequestMapping(value = "/members", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Member> update(@Valid @RequestBody MemberDTO member) throws URISyntaxException {
		log.debug("REST request to update Member : {}", member);
		if (member.getId() == null) {
			return create(member);
		}
		Member result = memberService.doSave(member);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("member", member.getId().toString()))
				.body(result);
	}
	
	/**
	 * GET /members -> get all the members.
	 */
	@RequestMapping(value = "/members", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MemberDTO>> getAll(
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "include_actif", required = false) boolean includeActif,
			@RequestParam(value = "include_not_enabled", required = false) boolean includeNotEnabled,
			@RequestParam(value = "include_bloque", required = false) boolean includeBloque) throws URISyntaxException {
		Pageable generatePageRequest = PaginationUtil.generatePageRequest(offset, limit);
		
		List<MemberDTO> page = doFindAll(generatePageRequest, search, includeActif, includeNotEnabled, includeBloque );
//		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/members", offset, limit);
		return new ResponseEntity<>(page, HttpStatus.OK);
	}

	protected List<MemberDTO> doFindAll(Pageable generatePageRequest, String search,boolean includeActif,boolean includeNotEnabled,boolean includeBloque) {
		search = search == null ? "" :search;
		String customSearch = "%" + search.replaceAll("\\*", "%").toLowerCase() + "%";
		return memberRepository.findAll(
				boxService.findCurrentCrossFitBox(), customSearch, 
				includeActif, includeNotEnabled, includeBloque, generatePageRequest).stream().map(convert()).collect(Collectors.toList());
	}

	/**
	 * GET /members/:id -> get the "id" member.
	 */

	@RequestMapping(value = "/members/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MemberDTO> get(@PathVariable Long id) {
		log.debug("REST request to get Member : {}", id);
		return Optional.ofNullable(doGet(id))
				.map(member -> new ResponseEntity<>(member, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	protected MemberDTO doGet(Long id) {
		Member member = memberRepository.findOne(id);		
		MemberDTO memberDTO = convert().apply(member);
		
		memberDTO.setSubscriptions(subscriptionRepository.findAllByMember(member));
		
		return memberDTO;
	}

	/**
	 * DELETE /members/:id -> delete the "id" member.
	 */

	@RequestMapping(value = "/members/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete Member : {}", id);
		doDelete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("member", id.toString())).build();
	}

	protected void doDelete(Long id) {
		memberService.deleteMember(id);
	}
	

	@RequestMapping(value = "/members/{id}/resetaccount", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> reset(@PathVariable Long id) {
		log.debug("REST request to reset Member : {}", id);
		Member member = memberRepository.findOne(id);
		if (member != null){

			memberService.initAccountAndSendMail(member);

		}
		return ResponseEntity.ok().build();
	}
	@RequestMapping(value = "/members/massActivation", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> massActivation() {
		log.debug("Envoi du mail d'activation a tous les membres non actif");
		
		List<Member> allMembersNotActivated = memberRepository.findAllUserNotEnabled(boxService.findCurrentCrossFitBox());
		
		for (Member member : allMembersNotActivated) {
			
			member.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
			member.setLastModifiedDate(DateTime.now());
			
			
			try {
				memberService.initAccountAndSendMail(member);
			} catch (Exception e) {
				log.warn("Impossible d'envoyer le mail a {}", member.getLogin());
			}
		}
		return ResponseEntity.ok().build();
	}
	
	

	private Function<Member, MemberDTO> convert() {
		return (member)->{
			MemberDTO result = new MemberDTO();
			result.setId(member.getId());
			result.setFirstName(member.getFirstName());
			result.setLastName(member.getLastName());
			result.setTitle(member.getTitle());
			result.setAddress(member.getAddress());
			result.setZipCode(member.getZipCode());
			result.setCity(member.getCity());
			
			result.setEnabled(member.isEnabled());
			result.setLangKey(member.getLangKey());
			result.setLocked(member.isLocked());
			result.setEmail(member.getLogin());
			result.setTelephonNumber(member.getTelephonNumber());
			
			return result;
		};
	}
}
