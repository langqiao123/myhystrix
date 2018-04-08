package com.langqiao.myhytrix;

import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class CommandFallBack extends HystrixCommand<String>{

	private final String name;

	public CommandFallBack(String name) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(500))
						.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ExampleGroup-pool"))
						.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(4))); 
		this.name = name;
	}

	@Override
	protected String run() throws InterruptedException {
		System.out.println("running");
		TimeUnit.MILLISECONDS.sleep(1000);
		return "Hello " +  name + "!";
	}
	
	@Override
	protected String getFallback() {
		System.out.println("Hello "+"Fallback");
		return "Hello "+"Fallback";
	}
}