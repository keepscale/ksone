package org.crossfit.app.stats.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Authority;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.exception.EmailAlreadyUseException;
import org.crossfit.app.repository.AuthorityRepository;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.repository.PersistentTokenRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.util.RandomUtil;
import org.crossfit.app.stats.data.StackedData;
import org.crossfit.app.web.rest.dto.MemberDTO;
import org.crossfit.app.web.rest.dto.SubscriptionDTO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class MembershipStatsService {

    private final Logger log = LoggerFactory.getLogger(MembershipStatsService.class);


    @Inject
    private CrossFitBoxSerivce boxService;
    @Inject
    private SubscriptionRepository subscriptionRepository;
    
    public StackedData countSubscriptionsMonthByMonth(LocalDate startAd, LocalDate endAt){
    	CrossFitBox box = boxService.findCurrentCrossFitBox();
		List<Subscription> subscriptions = subscriptionRepository.findAllByBoxWithMembership(box).stream()
				.filter(s -> {
					return s.getSubscriptionStartDate().isBefore(endAt) && s.getSubscriptionEndDate().isAfter(endAt);
				}).collect(Collectors.toList());
		
		Optional<Subscription> findFirst = subscriptions.stream()
				.sorted((e1, e2) -> e1.getSubscriptionStartDate().compareTo(e2.getSubscriptionStartDate())).findFirst();
		
		Map<Membership, List<Subscription>> subscriptionByMembership = subscriptions.stream().collect(Collectors.groupingBy(Subscription::getMembership));
		
		StackedData datas = new StackedData();
		if (findFirst.isPresent()){
			for (Entry<Membership, List<Subscription>> entry : subscriptionByMembership.entrySet()) {
				LocalDate startMonthInclus = findFirst.get().getSubscriptionStartDate().withDayOfMonth(1);
				while (startMonthInclus.isBefore(endAt)) {
					long countSubscriptionAt = entry.getValue().stream().filter(PresentAt(startMonthInclus)).count();
					datas.putData(entry.getKey().getName(), startMonthInclus, countSubscriptionAt);
					startMonthInclus = startMonthInclus.plusMonths(1);
				}
			}
		}
		
		return datas;
    }

	private Predicate<? super Subscription> PresentAt(LocalDate date) {
		Predicate<? super Subscription> predicate = s->{
			return date.isEqual(s.getSubscriptionStartDate()) || 
					(date.isAfter(s.getSubscriptionStartDate()) && date.isBefore(s.getSubscriptionEndDate()));
		};
		return predicate;
	}


}
