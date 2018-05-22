package org.crossfit.app.web.rest.stats;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.domain.enumeration.MembershipRulesType;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.MemberService;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.stats.service.MembershipStatsService;
import org.crossfit.app.web.rest.dto.BookingDTO;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.crossfit.app.web.rest.dto.SubscriptionDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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
        LocalDate end = new LocalDate();
		return membershipStatsService.countSubscriptionsMonthByMonth(start, end).toJson().toString();
    }

}
