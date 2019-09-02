package org.crossfit.app.config;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.crossfit.app.web.filter.CachingHttpHeadersFilter;
import org.crossfit.app.web.filter.StaticResourcesProductionFilter;
import org.crossfit.app.web.filter.gzip.GZipServletFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements WebMvcConfigurer, ServletContextInitializer {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    @Inject
    private Environment env;

    
    
    @Override
	public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/front-app/").setViewName(
                "forward:/front-app/index.html");
        registry.addViewController("/").setViewName(
                "forward:/index.html");
	}


	@Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("Web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
        servletContext.addListener(RequestContextListener.class);

        EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        
        initCachingHttpHeadersFilter(servletContext, disps);
        initRedirectToOtherApplicationFilter(servletContext, disps);
//        initStaticResourcesProductionFilter(servletContext, disps);
        initGzipFilter(servletContext, disps);
        
        log.info("Web application fully configured");
    }


    /**
     * Initializes the RedirectToOtherApplicationVersion filter.
     */
    private void initRedirectToOtherApplicationFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Registering RedirectToOtherApplicationVersion Filter");
        
        FilterRegistration.Dynamic redirectFilter =
                servletContext.addFilter("delegateFilter", new DelegatingFilterProxy("redirectToOtherApplicationVersionFilter"));

        Map<String, String> parameters = new HashMap<>();
        redirectFilter.setInitParameters(parameters);
        redirectFilter.addMappingForUrlPatterns(disps, true, "/");
        redirectFilter.setAsyncSupported(true);
    }
    
    

    /**
     * Initializes the GZip filter.
     */
    private void initGzipFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
        log.debug("Registering GZip Filter");
        FilterRegistration.Dynamic compressingFilter = servletContext.addFilter("gzipFilter", new GZipServletFilter());
        Map<String, String> parameters = new HashMap<>();
        compressingFilter.setInitParameters(parameters);
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.css");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.json");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.html");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.js");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.svg");
        compressingFilter.addMappingForUrlPatterns(disps, true, "*.ttf");
        compressingFilter.addMappingForUrlPatterns(disps, true, "/api/*");
        compressingFilter.addMappingForUrlPatterns(disps, true, "/private/*");
        compressingFilter.addMappingForUrlPatterns(disps, true, "/protected/*");
        compressingFilter.setAsyncSupported(true);
    }

    /**
     * Initializes the static resources production Filter.
     */
    private void initStaticResourcesProductionFilter(ServletContext servletContext,
                                                     EnumSet<DispatcherType> disps) {

        log.debug("Registering static resources production Filter");
        FilterRegistration.Dynamic staticResourcesProductionFilter =
                servletContext.addFilter("staticResourcesProductionFilter",
                        new StaticResourcesProductionFilter());

        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/bower_components/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/i18n/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/assets/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/scripts/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/front-app/*");
        staticResourcesProductionFilter.setAsyncSupported(true);
    }

    /**
     * Initializes the caching HTTP Headers Filter.
     */
    private void initCachingHttpHeadersFilter(ServletContext servletContext,
                                              EnumSet<DispatcherType> disps) {
        log.debug("Registering Caching HTTP Headers Filter");
        FilterRegistration.Dynamic cachingHttpHeadersFilter =
                servletContext.addFilter("cachingHttpHeadersFilter",
                        new CachingHttpHeadersFilter(env));

        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/assets/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/scripts/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/front-app/*");
        cachingHttpHeadersFilter.setAsyncSupported(true);
    }
    
}
