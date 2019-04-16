package org.crossfit.app.web.rest.api;

import java.net.URISyntaxException;

import javax.inject.Inject;

import org.crossfit.app.domain.Mandate;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.exception.AlreadySignedException;
import org.crossfit.app.service.SignatureService;
import org.crossfit.app.web.rest.dto.MandateDTO;
import org.crossfit.app.web.rest.dto.SubscriptionDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Subscription.
 */
@RestController
@RequestMapping("/api")
public class SignatureResource {

	private final Logger log = LoggerFactory.getLogger(SignatureResource.class);

	@Inject
	private SignatureService subscriptionService;


	@RequestMapping(value = "/sign/subscription", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SubscriptionDTO> sign(@RequestBody SubscriptionDTO dto) throws URISyntaxException, AlreadySignedException {
		log.debug("REST request to sign Subscription : {}", dto);
		
		Subscription result = subscriptionService.sign(dto);
		
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("subscription", result.getId().toString()))
				.body(SubscriptionDTO.fullMapper.apply(result));
	}
	


	@RequestMapping(value = "/sign/mandate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MandateDTO> sign(@RequestBody MandateDTO dto) throws URISyntaxException, AlreadySignedException {
		log.debug("REST request to sign Mandate : {}", dto);
		
		Mandate result = subscriptionService.sign(dto);
		
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("subscription", result.getId().toString()))
				.body(MandateDTO.fullMapper.apply(result));
	}
}
