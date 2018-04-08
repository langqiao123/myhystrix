package com.langqiao.myhytrix;

import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class CommandHelloFailure extends HystrixCommand<String> {
	 
    private final String name;
 
    public CommandHelloFailure(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))  //必须
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(1000))  //超时时间
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ExampleGroup-pool"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(3)));
 
        this.name = name;
    }
 
    @Override
    protected String run() throws InterruptedException {
        String theadName = this.getThreadPoolKey().name();
        String cmdKey=this.getThreadPoolKey().name();
        System.out.println("running begin , threadPool="+theadName+" cmdKey="+cmdKey+" name="+name);
 
        if("Exception".equals(name)) {
            throw new RuntimeException("this command always fails");
        }else if("Timeout".equals(name)){
            TimeUnit.SECONDS.sleep(2);
        }else if("Reject".equals(name)){
            TimeUnit.MILLISECONDS.sleep(800);
        }
        System.out.println(" run end");
 
        return "Hello " + name + "!";
    }
 
    @Override
    protected String getFallback() {
        StringBuilder sb = new StringBuilder("running fallback");
        boolean isRejected = isResponseRejected();
        boolean isException = isFailedExecution();
        boolean isTimeout= isResponseTimedOut();
        boolean isCircut = isCircuitBreakerOpen();
 
        sb.append(", isRejected:").append(isRejected);
        sb.append(", isException:"+isException);
        if(isException){
            sb.append(" msg=").append(getExecutionException().getMessage());
        }
        sb.append(",  isTimeout: "+isTimeout);
        sb.append(",  isCircut:"+isCircut);
 
        sb.append(", group:").append(this.getCommandGroup().name());
        sb.append(", threadpool:").append(getThreadPoolKey().name());
        System.out.println(sb.toString());
 
        String msg="Hello Failure " + name + "!";
        return msg;
    }
}
