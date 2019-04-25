package org.crossfit.app.web.rest.stats;

import org.crossfit.app.stats.service.MembershipStatsService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/stats")
public class StatsResource {

    private final Logger log = LoggerFactory.getLogger(StatsResource.class);

    @Inject
    private MembershipStatsService membershipStatsService;


    /**
     * GET  /membership
     */
    @RequestMapping(value = "/membership",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String membership() {
        log.debug("REST request to get membership stats");
        LocalDate start = new LocalDate(0);
        LocalDate end = new LocalDate().plusMonths(6);
		return membershipStatsService.countSubscriptionsMonthByMonth(start, end).toJson().toString();
    }

}
