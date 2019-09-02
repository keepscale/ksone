package org.crossfit.app.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.exception.CrossFitBoxConfiguration;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.CrossFitBoxRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.service.box.CrossFitBoxResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrossFitBoxSerivce {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxSerivce.class);

    
	@Autowired
	private CrossFitBoxResolverService crossFitBoxResolverService;
	
	@Autowired
	private HttpServletRequest request;
	
	public CrossFitBox findCurrentCrossFitBox(){
		return crossFitBoxResolverService.findCrossFitBoxByServername(request.getServerName());
	}
}
