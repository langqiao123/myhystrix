package com.langqiao.myhytrix;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import rx.Observable;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class CommandHelloWorld extends HystrixCommand<String>{

	private final String name;

	public CommandHelloWorld(String name) {
		super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")); // 必须
		this.name = name;
	}

	@Override
	protected String run() {
		/*
		 * 网络调用 或者其他一些业务逻辑，可能会超时或者抛异常
		 */
		return "Hello " + name + "!";
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		String s = new CommandHelloWorld("Bob").execute(); 
		System.out.println("s="+s);
		
		Future<String> s1 = new CommandHelloWorld("Bob").queue();
		System.out.println("s1="+s1.get());
		
		Observable<String> s2 = new CommandHelloWorld("Bob").observe();
		System.out.println("s2="+s2);
		
		Observable<String> s3 = new CommandHelloWorld("Bob").toObservable();
		System.out.println("s3="+s3);
	}
}
