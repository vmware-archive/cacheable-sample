package io.pivotal.pde.sample.cacheable;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExchangeRate 
{	
    public static void main( String[] args )
    {
    	if (args.length < 2){
    		System.out.println("please provide from currency and to currency arguments (e.g. exchangerate EUR USD)");
    		System.exit(1);
    	}
    	
    	String from = args[0];
    	String to = args[1];
    	
    	ClassPathXmlApplicationContext ctx= new ClassPathXmlApplicationContext("context.xml");
    	DummyExchangeRateProvider provider = ctx.getBean(DummyExchangeRateProvider.class);
    	
    	double rate = provider.getExchangeRate(from, to);
    	System.out.println(rate);
    }
}
