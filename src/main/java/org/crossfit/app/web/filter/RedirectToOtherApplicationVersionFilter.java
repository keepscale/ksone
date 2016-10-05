package org.crossfit.app.web.filter;


import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This filter is used to redirect the http request to another domain
 * </p>
 */
@Component("redirectToOtherApplicationVersionFilter")
public class RedirectToOtherApplicationVersionFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(RedirectToOtherApplicationVersionFilter.class);
    
    private static final String[] EXCLUDES_PATTERNS = {"/scripts/", "/i18n/", "/assets/", "/bower_components/"};
    
	@Autowired
	private CrossFitBoxSerivce boxService;
	
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to initialize
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
				
		String requestURI = httpRequest.getRequestURI();
		
		if (StringUtils.containsAny(requestURI, EXCLUDES_PATTERNS)){
			chain.doFilter(request, response);
			return;
		}
		
    	
        CrossFitBox box = boxService.findCurrentCrossFitBox();
        String redirectToRules = box.getRedirectToRules();
        
        if (StringUtils.isNotBlank(redirectToRules)){
        	

			String serverName = httpRequest.getServerName();

        	String[] rules = StringUtils.splitByWholeSeparator(redirectToRules, "\n");

        	log.debug("Filtering {} on server {} with rules: {}", httpRequest.getRequestURL(), serverName, Arrays.asList(rules));
        	
			
        	
        	for (String rule : rules) {
				String[] redirectRule = StringUtils.splitByWholeSeparator(rule, "->", 2);
				String from = redirectRule[0];
				String to = redirectRule[1];
				
				if (serverName.equalsIgnoreCase(from)){
					if (!StringUtils.startsWith(requestURI, "/")){
						requestURI = "/" + requestURI;
					}
					String location = httpRequest.getScheme() + "://" + to + requestURI;
					log.debug("Redirect to " + location);
					httpResponse.sendRedirect(location);
					return;
				}
			}
        }

		chain.doFilter(request, response);
        
    }
}
