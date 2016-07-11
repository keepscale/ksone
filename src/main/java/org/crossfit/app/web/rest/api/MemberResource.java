package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.AuthorityRepository;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MailService;
import org.crossfit.app.service.util.RandomUtil;
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
	private MemberRepository memberRepository;
    @Inject
    private CrossFitBoxSerivce boxService;
    @Inject
    private PasswordEncoder passwordEncoder;
    @Inject
    private AuthorityRepository authorityRepository;
    @Inject
	private MailService mailService;
    
    
	/**
	 * POST /members -> Create a new member.
	 */
	@RequestMapping(value = "/members", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
		if (member.getId() == null){
			
			member.setAuthorities(new HashSet<Authority>(Arrays.asList(authorityRepository.findOne(AuthoritiesConstants.USER))));
			member.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
			member.setCreatedDate(DateTime.now());			
			member.setBox(boxService.findCurrentCrossFitBox());
			

			initAccountAndSendMail(member);
		}
		else{
			//Les seuls champs modifiable de user, c'est le nom, le prénom & l'email
			String firstName = member.getFirstName();
			String lastName = member.getLastName();
			String telephonNumber = member.getTelephonNumber();
			String login = member.getLogin();
			
			Member actualMember = memberRepository.findOne(member.getId());
			actualMember.setFirstName(firstName);
			actualMember.setLastName(lastName);
			actualMember.setTelephonNumber(telephonNumber);
			actualMember.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
			actualMember.setLastModifiedDate(DateTime.now());
			
			
			//L'email a changé ? on repasse par une validation d'email
			if (!login.equals(actualMember.getLogin())){
				actualMember.setLogin(login.toLowerCase());
				initAccountAndSendMail(member);
			}
		}

		
		return memberRepository.save(member);
	}

	protected void initAccountAndSendMail(Member member) {
		String generatePassword = RandomUtil.generatePassword();
		member.setLogin(member.getLogin().toLowerCase());
		member.setPassword(passwordEncoder.encode(generatePassword));
		member.setEnabled(false);
		member.setLocked(false);

		mailService.sendActivationEmail(member, generatePassword);
	}

	/**
	 * PUT /members -> Updates an existing member.
	 */

	@RequestMapping(value = "/members", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
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
	public ResponseEntity<List<Member>> getAll(@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
		Pageable generatePageRequest = PaginationUtil.generatePageRequest(offset, limit);
		Page<Member> page = doFindAll(generatePageRequest);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/members", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	protected Page<Member> doFindAll(Pageable generatePageRequest) {
		return memberRepository.findAll(boxService.findCurrentCrossFitBox(), generatePageRequest);
	}

	/**
	 * GET /members/:id -> get the "id" member.
	 */

	@RequestMapping(value = "/members/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete Member : {}", id);
		doDelete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("member", id.toString())).build();
	}

	protected void doDelete(Long id) {
		boxService.deleteMember(id);
	}
	

	@RequestMapping(value = "/members/{id}/resetaccount", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> reset(@PathVariable Long id) {
		log.debug("REST request to reset Member : {}", id);
		Member member = doGet(id);
		if (member != null){
			member.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
			member.setLastModifiedDate(DateTime.now());
		
			initAccountAndSendMail(member);

			try {
				memberRepository.save(member);
			} catch (Exception e) {
				log.warn("Impossible d'envoyer le mail a {}", member.getLogin());
			}
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
			
			initAccountAndSendMail(member);
			
			try {
				memberRepository.save(member);
			} catch (Exception e) {
				log.warn("Impossible d'envoyer le mail a {}", member.getLogin());
			}
		}
		return ResponseEntity.ok().build();
	}
}
