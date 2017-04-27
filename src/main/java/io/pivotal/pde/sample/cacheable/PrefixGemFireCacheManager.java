package io.pivotal.pde.sample.cacheable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Region;

/**
 * The default Spring Data GemFire cache manager equates the GemFire
 * region name with the Spring Cache abstraction's cache name.  This 
 * implementation derives the region name form the cache name, which 
 * is provided in the @Cacheable annotation as usual, and a prefix
 * that is configured during Spring context initialization via properties. 
 * 
 * This allows the exact same code (which includes the @Cacheable annotation) 
 * to target different regions based on startup configuration.
 * 
 * @author rmay
 *
 */
public class PrefixGemFireCacheManager implements CacheManager {

	private String prefix = "";
	
	private Map<String, Cache> caches;

	public PrefixGemFireCacheManager(){
		caches = new HashMap<String,Cache>(5);
	}
	
	public void setPrefix(String prefix){
		this.prefix = prefix;
	}
	
	/**
	 * Retrieves the cache with the given name.
	 * 
	 * This method will throw a RuntimeException if a backing region 
	 * named prefix + name is not present.
	 */
	@Override
	public Cache getCache(String name) {
		Cache result = null;
		synchronized(caches){
			if (caches.containsKey(name)){
				result = caches.get(name);
			} else {
				Region<?,?> region = CacheFactory.getAnyInstance().getRegion(prefix + name);
				if (region == null){
					throw new RuntimeException("There is no backing region defined for this cache.  Expected a region named: " + prefix + name);
				}
				result = new NamedGemFireCache(region, name);
				caches.put(name, result);
			}
		}
		
		return result;
	}
	
	/*
	 * Developer Note
	 * 
	 * It would be possible to have the client side region created dynamically
	 * so that there would be not need to declare it explicitly. 
	 */

	@Override
	public Collection<String> getCacheNames() {
		return caches.keySet();
	}

}
