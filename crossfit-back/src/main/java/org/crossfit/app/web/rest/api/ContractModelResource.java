package org.crossfit.app.web.rest.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.SubscriptionContractModel;
import org.crossfit.app.service.SubscriptionContractModelService;
import org.crossfit.app.web.rest.dto.SubscriptionContractModelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/api")
public class ContractModelResource {

	private final Logger log = LoggerFactory.getLogger(ContractModelResource.class);


    @Inject
    private SubscriptionContractModelService contractModelService;

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
     * GET /contractmodels -> get all the contractmodels.
     */
    @RequestMapping(value = "/contractmodels/{id}/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String geContractModelData(@PathVariable Long id) {
        log.debug("REST request to get all contractmodels");
        return contractModelService.getJsonById(id);
    }

}
