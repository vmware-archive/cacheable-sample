package io.pivotal.pde.sample.cacheable;

import org.springframework.data.gemfire.support.GemfireCache;

import com.gemstone.gemfire.cache.Region;

/*
 * This class is a minor extension of the spring-data-gemfire GemfireCache
 * which allows the cache name and the region name to be different.
 */
public class NamedGemFireCache extends GemfireCache {
	
	private String name;
	
	public NamedGemFireCache(Region<?,?> region, String name){
		super(region);
		this.name = name;
	}
	
	@Override
	public String getName(){
		return this.name;
	}

	
}
