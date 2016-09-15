package org.crossfit.app.web.rest.card;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    
	/**
	 * GET /members -> get all the members.
	 */
	@RequestMapping(value = "/members", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<MemberDTO>> getAll(
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "search", required = false) String search) throws URISyntaxException {
		Pageable generatePageRequest = PaginationUtil.generatePageRequest(offset, limit);
		
		List<MemberDTO> list = doFindAll(generatePageRequest, search, true, true, true);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders( new PageImpl<MemberDTO>(list), "/api/members", offset, limit);
		return new ResponseEntity<>(list, headers, HttpStatus.OK);
	}

	protected List<MemberDTO> doFindAll(Pageable generatePageRequest, String search,boolean includeActif,boolean includeNotEnabled,boolean includeBloque) {
		search = search == null ? "" :search;
		String customSearch = "%" + search.replaceAll("\\*", "%").toLowerCase() + "%";
		return memberRepository.findAll(
				boxService.findCurrentCrossFitBox(), customSearch, 
				includeActif, includeNotEnabled, includeBloque, generatePageRequest).stream().map(MemberDTO.MAPPER).collect(Collectors.toList());
	}

	
    /**
     * PUT  /account -> update the current user information.
     */
    @RequestMapping(value = "/members/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@RequestBody String cardUuid, @PathVariable Long id) {
        log.debug("REST request to update cardUuid of Member : {}", id);

        memberRepository.clearUsageCardUuid(cardUuid);
        memberRepository.updateCardUuid(id, cardUuid);
        
        return ResponseEntity.ok().build();
    }
	
}
