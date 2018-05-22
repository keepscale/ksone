package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CardEvent;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.CardEventRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MemberService;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.web.rest.dto.BookingDTO;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.crossfit.app.web.rest.dto.SubscriptionDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

	public enum HealthIndicator {
		SUBSCRIPTIONS_OVERLAP, SUBSCRIPTIONS_OVERLAP_NON_RECURRENT, NO_SUBSCRIPTION, NO_ADDRESS, NO_CARD, SUBSCRIPTIONS_BAD_INTERVAL, SUBSCRIPTIONS_NOT_END_AT_END_MONTH;

	}
	public enum CustomCriteria {
		EXPIRE
	}

	private final Logger log = LoggerFactory.getLogger(MemberResource.class);
	@Inject
	private CrossFitBoxSerivce boxService;
	@Inject
	private TimeService timeService;
	@Inject
	private MemberService memberService;
	@Inject
	private MemberRepository memberRepository;
    @Inject
    private CardEventRepository cardEventRepository;

    @Inject
    private SubscriptionRepository subscriptionRepository;
    @Inject
    private BookingRepository bookingRepository;
    
    
	/**
	 * POST /members -> Create a new member.
	 */
	@RequestMapping(value = "/members", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MemberDTO> create(@Valid @RequestBody MemberDTO member) throws URISyntaxException {
		log.debug("REST request to save Member : {}", member);
		if (member.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new member cannot already have an ID").body(null);
		}
		MemberDTO result = MemberDTO.MAPPER.apply(memberService.doSave(member));
		return ResponseEntity.created(new URI("/api/members/" + result.getId()))
				.headers(HeaderUtil.createEntityCreationAlert("member", result.getId().toString())).body(result);
	}

	
	/**
	 * PUT /members -> Updates an existing member.
	 */

	@RequestMapping(value = "/members", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MemberDTO> update(@Valid @RequestBody MemberDTO member) throws URISyntaxException {
		log.debug("REST request to update Member : {}", member);
		if (member.getId() == null) {
			return create(member);
		}
		MemberDTO result = MemberDTO.MAPPER.apply(memberService.doSave(member));
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("member", member.getId().toString()))
				.body(result);
	}

	/**
	 * GET /members/quick -> get all the members.
	 */
	@RequestMapping(value = "/members/quick", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MemberDTO>> getAllQuick(
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "include_actif", required = false) boolean includeActif,
			@RequestParam(value = "include_not_enabled", required = false) boolean includeNotEnabled,
			@RequestParam(value = "include_bloque", required = false) boolean includeBloque) throws URISyntaxException {
		Pageable generatePageRequest = PaginationUtil.generatePageRequest(offset, limit);
		
		Page<Member> page = doFindAll(generatePageRequest, search, true, true, includeActif, includeNotEnabled, includeBloque );
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/members", offset, limit);
		return new ResponseEntity<>(page.map(MemberDTO.CONVERTER).getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /members -> get all the members.
	 */
	@RequestMapping(value = "/members", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MemberDTO>> getAll(
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "include_memberships", required = false) Long[] includeMembershipsIds,
			@RequestParam(value = "include_roles", required = false) String[] includeRoles,
			@RequestParam(value = "with_healthindicators", required = false) HealthIndicator[] withHealthIndicators,
			@RequestParam(value = "with_customcriteria", required = false) CustomCriteria[] withCustomCriteria,
			@RequestParam(value = "with_customcriteria_expire", required = false) String withCustomCriteriaExpireAt,
			@RequestParam(value = "include_actif", required = false) boolean includeActif,
			@RequestParam(value = "include_not_enabled", required = false) boolean includeNotEnabled,
			@RequestParam(value = "include_bloque", required = false) boolean includeBloque) throws URISyntaxException {
		Pageable generatePageRequest = PaginationUtil.generatePageRequest(offset, limit);
		
		Page<Member> page = doFindAll(generatePageRequest, search, includeMembershipsIds, includeRoles, withHealthIndicators, withCustomCriteria, withCustomCriteriaExpireAt, includeActif, includeNotEnabled, includeBloque );
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/members", offset, limit);
		return new ResponseEntity<>(page.map(MemberDTO.CONVERTER).getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /members -> get all the members.
	 */
	@RequestMapping(value = "/members.csv", method = RequestMethod.GET, produces = "text/csv;charset=utf-8")
	public String getAllAsCSV(
			@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "include_memberships", required = false) Long[] includeMembershipsIds,
			@RequestParam(value = "include_roles", required = false) String[] includeRoles,
			@RequestParam(value = "with_healthindicators", required = false) HealthIndicator[] withHealthIndicators,
			@RequestParam(value = "with_customcriteria", required = false) CustomCriteria[] withCustomCriteria,
			@RequestParam(value = "with_customcriteria_expire", required = false) String withCustomCriteriaExpireAt,
			@RequestParam(value = "include_actif", required = false) boolean includeActif,
			@RequestParam(value = "include_not_enabled", required = false) boolean includeNotEnabled,
			@RequestParam(value = "include_bloque", required = false) boolean includeBloque) throws URISyntaxException {
		
		Pageable generatePageRequest =  new PageRequest(0, Integer.MAX_VALUE);
		
		Page<Member> page = doFindAll(generatePageRequest, search, includeMembershipsIds, includeRoles, withHealthIndicators, withCustomCriteria, withCustomCriteriaExpireAt, includeActif, includeNotEnabled, includeBloque );
		
		StringBuffer sb = new StringBuffer();

		sb.append("[Id];[Title];[FirstName];[LastName];[Email];[CardNumber];[Telephon];[Address];[ZipCode];[City]\n");		
		for (Member m : page) {
			append(sb, m.getId()).append(";");
			append(sb, m.getTitle()).append(";");
			append(sb, m.getFirstName()).append(";");
			append(sb, m.getLastName()).append(";");
			append(sb, m.getLogin()).append(";");
			append(sb, m.getCardUuid()).append(";");
			append(sb, m.getTelephonNumber()).append(";");
			append(sb, m.getAddress()).append(";");
			append(sb, m.getZipCode()).append(";");
			append(sb, m.getCity()).append("\n");
		}
		return sb.toString();
	}
	
	private static final StringBuffer append(StringBuffer sb, Object value){
		sb.append("\"").append(value == null ? "" : value).append("\"");
		return sb;
	}


	private Page<Member> doFindAll(Pageable generatePageRequest, String search, boolean includeAllMemberships, boolean includeAllRoles,
			boolean includeActif, boolean includeNotEnabled, boolean includeBloque) {
		return doFindAll(generatePageRequest, search, includeAllMemberships, new Long[]{} , includeAllRoles, new String[]{} , null, null, null, includeActif, includeNotEnabled, includeBloque);
	}
	protected Page<Member> doFindAll(Pageable generatePageRequest, String search, Long[] includeMembershipsIds, String[] includeRoles, HealthIndicator[] withHealthIndicators, 
			CustomCriteria[] withCustomCriteria, String withCustomCriteriaExpireAt, boolean includeActif,boolean includeNotEnabled,boolean includeBloque) {
		return doFindAll(generatePageRequest, search, false, includeMembershipsIds, false, includeRoles, withHealthIndicators,withCustomCriteria, withCustomCriteriaExpireAt,  includeActif, includeNotEnabled, includeBloque);
	}
		
	private Page<Member> doFindAll(Pageable generatePageRequest, String search, boolean includeAllMemberships, Long[] includeMembershipsIds, boolean includeAllRoles, String[] includeRoles, HealthIndicator[] withHealthIndicators, 
			CustomCriteria[] withCustomCriteria, String withCustomCriteriaExpireAt, boolean includeActif,boolean includeNotEnabled,boolean includeBloque) {
		search = search == null ? "" :search;
		String customSearch = "%" + search.replaceAll("\\*", "%").toLowerCase() + "%";
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		List<Member> members = memberRepository.findAll(
				currentCrossFitBox, customSearch, 
				Stream.of(includeMembershipsIds != null && includeMembershipsIds.length > 0 ? includeMembershipsIds : new Long[]{-1L}).collect(Collectors.toSet()), 
				includeAllMemberships,
				Stream.of(includeRoles != null && includeRoles.length > 0 ? includeRoles : new String[]{""}).collect(Collectors.toSet()), 
				includeAllRoles,
				includeActif, includeNotEnabled, includeBloque);
		
		if (withHealthIndicators != null && withHealthIndicators.length > 0) {
			Map<HealthIndicator, List<Member>> calculateHealthIndicators = calculateHealthIndicators(withHealthIndicators);
			Set<Member> memberConcernedByHealth = calculateHealthIndicators.values().stream().flatMap(l->l.stream()).collect(Collectors.toSet());
			members.removeIf(m->!memberConcernedByHealth.contains(m));
		}
		
		if (withCustomCriteria != null && withCustomCriteria.length > 0) {

			List<CustomCriteria> withCustomCriteriaAsList = Arrays.asList(withCustomCriteria);
			if (withCustomCriteriaAsList.contains(CustomCriteria.EXPIRE) && StringUtils.isNotBlank(withCustomCriteriaExpireAt)) {
		    	DateTime atDate = timeService.parseDate("yyyy-MM-dd", withCustomCriteriaExpireAt, currentCrossFitBox);
				Set<Member> membersWithNoSub = memberService.findAllMemberWithNoActiveSubscriptionAtDate(atDate.toLocalDate()).stream().collect(Collectors.toSet());
				members.removeIf(m->!membersWithNoSub.contains(m));
			}
			
		}
		
		
		int start = generatePageRequest.getPageNumber() * generatePageRequest.getPageSize();
		int end = (generatePageRequest.getPageNumber()+1) * generatePageRequest.getPageSize();
		
		start = start < 0 ? 0 : start;
		end = end >= members.size() ? members.size() : end;
		
		return new PageImpl<>(members.isEmpty() ? members : members.subList(start, end), generatePageRequest, members.size());
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
	
    @RequestMapping(value = "/members/{id}/bookings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookingDTO>> getAllBooking(@PathVariable Long id) throws URISyntaxException {
    	
    	CrossFitBox box = boxService.findCurrentCrossFitBox();
		Member member = memberRepository.findOne(id);
		
		log.debug("getAllBooking() - Loading event cards"); 
		
		Map<Booking, List<CardEvent>> cardEventsByBooking = 
    			cardEventRepository.findAllBookingCardEventByMember(member)
				.stream()
				.collect(Collectors.groupingBy(CardEvent::getBooking));

		log.debug("getAllBooking() - Loading booking"); 
		
		List<Booking> result = bookingRepository.findAllByMember(member).stream().map(b->{
    	    	    			List<CardEvent> event = cardEventsByBooking.getOrDefault(b, Collections.emptyList());
								b.setCardEvent(event.stream().sorted((e1, e2)->{
    	    	    				return e1.getCheckingDate().compareTo(e2.getCheckingDate());
	    	    					}).findFirst());
    	    	    			return b;
    	    	    		}).collect(Collectors.toList());
		
		log.debug("getAllBooking() - to dto"); 

        Comparator<? super BookingDTO> comparator = (b1,b2) ->{
        	return b2.getStartAt().compareTo(b1.getStartAt());
        };
		return new ResponseEntity<>(result.stream().map(BookingDTO.memberEditMapper).sorted(comparator).collect(Collectors.toList()), HttpStatus.OK);
    }


	protected MemberDTO doGet(Long id) {
		Member member = memberRepository.findOne(id);		
		MemberDTO memberDTO = MemberDTO.MAPPER.apply(member);

		memberDTO.setRoles(member.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList()));
		memberDTO.setSubscriptions(new ArrayList<>(subscriptionRepository.findAllByMember(member).stream().map(s ->{
			
			SubscriptionDTO dto = SubscriptionDTO.fullMapper.apply(s);
			dto.setBookingCount(bookingRepository.countBySubscription(s));
			return dto;
			
		})
				.sorted((s1,s2)->{
					int res = s1.getSubscriptionStartDate().compareTo(s2.getSubscriptionStartDate());
					res = res != 0 ? res : s1.getSubscriptionEndDate().compareTo(s2.getSubscriptionEndDate()); 
					return res;
				})
				.collect(Collectors.toList())));
		
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

	@RequestMapping(value = "/members/{id}/lock", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> lock(@PathVariable Long id) {
		log.debug("REST request to reset Member : {}", id);
		Member member = memberRepository.findOne(id);
		if (member != null){
			memberService.lockUser(member);
		}
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping(value = "/members/massActivation", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> massActivation() {
		log.debug("Envoi du mail d'activation a tous les membres non actif");
		
		List<Member> allMembersNotActivated = memberRepository.findAllUserNotEnabled(boxService.findCurrentCrossFitBox());
		
		for (Member member : allMembersNotActivated) {
			
			member.setLastModifiedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
			member.setLastModifiedDate(DateTime.now(DateTimeZone.UTC));
			
			
			try {
				memberService.initAccountAndSendMail(member);
			} catch (Exception e) {
				log.warn("Impossible d'envoyer le mail a {}", member.getLogin());
			}
		}
		return ResponseEntity.ok().build();
	}
	
	/**
	 * GET /members/healthIndicators -> get all the paymentMethods.
	 */
	@RequestMapping(value = "/members/healthIndicators", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HealthIndicator[]> getHealthIndicators(){
		return new ResponseEntity<>(HealthIndicator.values(), HttpStatus.OK);
	}
	
	/**
	 * GET /members/health -> get all the members.
	 */
	@RequestMapping(value = "/members/health", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<HealthIndicator, Integer>> getAllMemberWithBadHealth(){
		
		Map<HealthIndicator, List<Member>> results = calculateHealthIndicators(HealthIndicator.values());
		
		Map<HealthIndicator, Integer> resultsdto = results.entrySet().stream()
		        .collect(Collectors.toMap(
		                e -> e.getKey(),
		                e -> e.getValue().size()
		            ));
		return ResponseEntity.ok(resultsdto);
	}


	private Map<HealthIndicator, List<Member>> calculateHealthIndicators(HealthIndicator[] forHealthIndicators) {
		Map<HealthIndicator, List<Member>> results = new HashMap<>();

		List<HealthIndicator> asList = Arrays.asList(forHealthIndicators);
		
		if (asList.contains(HealthIndicator.SUBSCRIPTIONS_OVERLAP)) {
			results.put(HealthIndicator.SUBSCRIPTIONS_OVERLAP, 
					memberService.findAllWithDoubleSubscription(HealthIndicator.SUBSCRIPTIONS_OVERLAP));
		}

		if (asList.contains(HealthIndicator.SUBSCRIPTIONS_OVERLAP_NON_RECURRENT)) {
			results.put(HealthIndicator.SUBSCRIPTIONS_OVERLAP_NON_RECURRENT, 
					memberService.findAllWithDoubleSubscription(HealthIndicator.SUBSCRIPTIONS_OVERLAP_NON_RECURRENT));
		}

		if (asList.contains(HealthIndicator.SUBSCRIPTIONS_BAD_INTERVAL)) {
			results.put(HealthIndicator.SUBSCRIPTIONS_BAD_INTERVAL, 
					memberService.findAllWithSubscriptionEndBeforeStart());
		}

		if (asList.contains(HealthIndicator.SUBSCRIPTIONS_NOT_END_AT_END_MONTH)) {
			results.put(HealthIndicator.SUBSCRIPTIONS_NOT_END_AT_END_MONTH, 
					memberService.findAllWithSubscriptionEndNotAtEndMonth());
		}

		if (asList.contains(HealthIndicator.NO_SUBSCRIPTION)) {
			results.put(HealthIndicator.NO_SUBSCRIPTION,
					memberService.findAllMemberWithNoActiveSubscriptionAtNow());
		}

		if (asList.contains(HealthIndicator.NO_CARD)) {
			results.put(HealthIndicator.NO_CARD,
					memberService.findAllMemberWithNoCard());
		}

		if (asList.contains(HealthIndicator.NO_ADDRESS)) {
			results.put(HealthIndicator.NO_ADDRESS,
					memberService.findAllMemberWithNoAddress());
		}
		return results;
	}

			
	

	
}
