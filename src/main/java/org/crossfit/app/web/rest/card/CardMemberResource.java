package org.crossfit.app.web.rest.card;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CardEvent;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.web.rest.card.dto.MemberCardDTO;
import org.crossfit.app.web.rest.dto.BookingDTO;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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

import reactor.bus.Event;
import reactor.bus.EventBus;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/card")
public class CardMemberResource {

	private final Logger log = LoggerFactory.getLogger(CardMemberResource.class);
	@Inject
	private CrossFitBoxSerivce boxService;
	
	@Inject	
	private MemberRepository memberRepository;  
	@Inject	
	private BookingRepository bookingRepository; 
	@Inject	
	private TimeService timeService;      

    @Inject
	private EventBus eventBus;
    
	/**
	 * GET /members -> get all the members.
	 */
	@RequestMapping(value = "/members", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MemberDTO>> getAll(
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false, defaultValue = "499") Integer limit,
			@RequestParam(value = "search", required = false) String search) throws URISyntaxException {
		Pageable generatePageRequest = PaginationUtil.generatePageRequest(offset, limit);
		
		Page<Member> page = doFindAll(generatePageRequest, search, true, true, true);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders( page, "/card/members", offset, limit);
		return new ResponseEntity<>(page.map(MemberDTO.CONVERTER).getContent(), headers, HttpStatus.OK);
	}

	protected Page<Member> doFindAll(Pageable generatePageRequest, String search, boolean includeActif,boolean includeNotEnabled,boolean includeBloque) {
		search = search == null ? "" :search;
		String customSearch = "%" + search.replaceAll("\\*", "%").toLowerCase() + "%";
		return memberRepository.findAll(
				boxService.findCurrentCrossFitBox(), customSearch,null, true, null, true,
				includeActif, includeNotEnabled, includeBloque, generatePageRequest);
	}


    /**
     * PUT  /account -> update the current user information.
     */
    @RequestMapping(value = "/members/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@RequestBody(required=false) String cardUuid, @PathVariable Long id) {
        log.debug("REST request to update cardUuid of Member : {}", id);

        memberRepository.clearUsageCardUuid(cardUuid);
        memberRepository.updateCardUuid(id, cardUuid);
        
        return ResponseEntity.ok().build();
    }
    

    /**
     * GET  /bookings/{carduuid} -> get the bookings of the user associated with card uuid.
     */
    @RequestMapping(value = "/{carduuid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberCardDTO> get(@PathVariable String carduuid,
    		@RequestParam(name="now", required=false) @DateTimeFormat(iso=ISO.DATE_TIME) Date date, 
    		@RequestParam(name="lessMinute", defaultValue="30", required=false) int lessMinute, 
    		@RequestParam(name="moreMinute", defaultValue="120", required=false) int moreMinute) {
        log.debug("REST request to get the bookings of the user associated with card uuid : {}", carduuid);

        CrossFitBox box = boxService.findCurrentCrossFitBox();
		Optional<Member> member = memberRepository.findOneByCardUuid(carduuid, box);

		DateTime nowAsDateTime = timeService.nowAsDateTime(box);
		DateTime checkForDate = date == null ? nowAsDateTime : new DateTime(date, timeService.getDateTimeZone(box));
		DateTime start = checkForDate.minusMinutes(lessMinute);
		DateTime end =   checkForDate.plusMinutes(moreMinute);

		
		log.debug("Il est {}. Check for {} (force={}) Recherche de resa entre le {} et le {} pour l'utilisateur {}", nowAsDateTime, checkForDate, date !=null, start, end, member.map(Member::getId).orElse(null));
		
		return member
		.map(m -> {
						
			Set<Booking> findAllStartBetween = bookingRepository.findAllStartBetween(box, m, start, end);
			if (findAllStartBetween.isEmpty()){
				CardEvent cardEvent = new CardEvent(nowAsDateTime, checkForDate, carduuid, box, m);
				eventBus.notify("checkingcard", Event.wrap(cardEvent));
			}
			else{
				for (Booking booking : findAllStartBetween) {
					log.debug("\tBooking [{}] [{}]", booking.getStartAt());
					
					CardEvent cardEvent = new CardEvent(nowAsDateTime, checkForDate, carduuid, box, m, booking);
					eventBus.notify("checkingcard", Event.wrap(cardEvent));
				}
			}
			
			
			
			List<BookingDTO> bookings = findAllStartBetween
					.stream().map(BookingDTO.cardMapper).collect(Collectors.toList());

			MemberCardDTO result = new MemberCardDTO(m, bookings);	
			
			
			return ResponseEntity.ok(result); 
		})
		.orElseGet(()->{
			CardEvent cardEvent = new CardEvent(nowAsDateTime, checkForDate, carduuid, box);
			eventBus.notify("checkingcard", Event.wrap(cardEvent));
			return new ResponseEntity<MemberCardDTO>(HttpStatus.NOT_FOUND);
		});
        
    }
	
}
