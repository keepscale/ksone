package org.crossfit.app.service.cache;

import javax.inject.Inject;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

	@Inject
	private CacheManager cacheManager;
	
	public void clearCache(String name) {
		cacheManager.getCache(name).clear();
	}
}
