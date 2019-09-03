package org.crossfit.app.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;


@Configuration
@EnableCaching
public class CacheConfiguration {

	public static final String BOX_CACHE_NAME = "boxs";
	public static final String HEALTH_CACHE_NAME = "health";
	
	@Bean
	public CacheManager cacheManager(Ticker ticker) {
	    CaffeineCache messageCache = buildCache(HEALTH_CACHE_NAME, ticker, 5);
	    CaffeineCache notificationCache = buildCache(BOX_CACHE_NAME, ticker, 60);
	    SimpleCacheManager manager = new SimpleCacheManager();
	    manager.setCaches(Arrays.asList(messageCache, notificationCache));
	    return manager;
	}
	 
	private CaffeineCache buildCache(String name, Ticker ticker, int minutesToExpire) {
	    return new CaffeineCache(name, Caffeine.newBuilder()
	                .expireAfterWrite(minutesToExpire, TimeUnit.MINUTES)
	                .maximumSize(100)
	                .ticker(ticker)
	                .build());
	}
	@Bean
	public Ticker ticker() {
	    return Ticker.systemTicker();
	}
	 
	
}
