package org.crossfit.app.web.rest.admin;

import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.PaymentMethod;
import org.crossfit.app.domain.enumeration.VersionFormatContractSubscription;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MembershipService;
import org.crossfit.app.service.SubscriptionContractModelService;
import org.crossfit.app.web.rest.dto.MembershipDTO;
import org.crossfit.app.web.rest.dto.SubscriptionContractModelDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/admin")
public class ContractModelResource {

	private final Logger log = LoggerFactory.getLogger(ContractModelResource.class);


    @Inject
    private SubscriptionContractModelService contractModelService;

    /**
     * POST /contractmodels -> Create a new contractmodel.
     */
    @RequestMapping(value = "/contractmodels", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SubscriptionContractModelDTO> create(@Valid @RequestBody SubscriptionContractModelDTO contractModelDTO)
            throws URISyntaxException {
        log.debug("REST request to save SubscriptionContractModel : {}", contractModelDTO);
        if (contractModelDTO.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new contractModelDTO cannot already have an ID")
                    .body(null);
        }
        SubscriptionContractModelDTO result = SubscriptionContractModelDTO.fullMapper.apply(contractModelService.save(contractModelDTO));
        return ResponseEntity.created(new URI("/admin/contractmodels/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("contractmodel", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT /contractmodels -> Updates an existing contractmodel.
     */
    @RequestMapping(value = "/contractmodels", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SubscriptionContractModelDTO> update(@Valid @RequestBody SubscriptionContractModelDTO contractModelDTO)
            throws URISyntaxException {
        log.debug("REST request to update SubscriptionContractModel : {}", contractModelDTO);
        if (contractModelDTO.getId() == null) {
            return create(contractModelDTO);
        }
        SubscriptionContractModelDTO result = SubscriptionContractModelDTO.fullMapper.apply(contractModelService.save(contractModelDTO));
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("contractmodel", result.getId().toString()))
                .body(result);
    }

    /**
     * GET /contractmodels -> get all the contractmodels.
     */
    @RequestMapping(value = "/contractmodels", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SubscriptionContractModelDTO> getAll() {
        log.debug("REST request to get all contractmodels");
        return contractModelService.findAllOfCurrentBox()
                .stream().map(SubscriptionContractModelDTO.fullMapper).collect(Collectors.toList());
    }


    /**
     * GET /contractmodels/versionformat -> get all the versionformats.
     */
    @RequestMapping(value = "/contractmodels/versionformat", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VersionFormatContractSubscription[]> getVersionFormatContractSubscriptions(){
        return new ResponseEntity<>(VersionFormatContractSubscription.values(), HttpStatus.OK);
    }
}
