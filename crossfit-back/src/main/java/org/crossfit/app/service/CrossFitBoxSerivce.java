package org.crossfit.app.service;

import javax.servlet.http.HttpServletRequest;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.service.box.CrossFitBoxResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service
@Transactional
public class CrossFitBoxSerivce {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxSerivce.class);

    
	@Autowired
	private CrossFitBoxResolverService crossFitBoxResolverService;
	
		
	public CrossFitBox findCurrentCrossFitBox(){
		return crossFitBoxResolverService.findCrossFitBoxByServername(this.getServerName());
	}
	private String getServerName() {
	    RequestAttributes attribs = RequestContextHolder.getRequestAttributes();
	    if (attribs instanceof NativeWebRequest) {
	        HttpServletRequest request = (HttpServletRequest) ((NativeWebRequest) attribs).getNativeRequest();
	        return request.getServerName();
	    }
	    return null;
	}
}
