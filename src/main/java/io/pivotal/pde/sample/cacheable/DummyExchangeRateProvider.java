package io.pivotal.pde.sample.cacheable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class DummyExchangeRateProvider {
	
	private Map<String, Double> rates;
	
	public DummyExchangeRateProvider(){
		Random rand = new Random();
		this.rates = new HashMap<String, Double>(30);
		String []currencies={"USD", "GBP", "AUD", "CAD", "EUR", "JPY"};
		for(int i=0;i<6;++i){
			String key = currencies[i] + "|" + currencies[i];
			rates.put(key, 1.0);
			for(int j=i+1; j < 6; ++j){
				 key = currencies[i] + "|" + currencies[j];
				 double val = 0.5 + 2.0 * rand.nextDouble();
				 rates.put(key, val);
				 key = currencies[j] + "|" + currencies[i];
				 val = 1.0 / val;
				 rates.put(key, val);				 
			}
		}
		
	}
	
	
	@Cacheable(cacheNames="Rates", key="#from.concat('|').concat(#to)")
	public double getExchangeRate(String from, String to){
		String conv = from.toUpperCase() + "|" + to.toUpperCase();
		Double result = rates.get(conv);
		if (result == null)
			throw new RuntimeException("Unknown conversion: " + conv);
		
		return result;
	}
}
