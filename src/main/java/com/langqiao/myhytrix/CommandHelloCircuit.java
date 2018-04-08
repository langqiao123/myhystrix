package com.langqiao.myhytrix;

import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class CommandHelloCircuit extends HystrixCommand<String> {
	 
    private final String name;
 
    public CommandHelloCircuit(String name) {
    	//在10s内，如果请求在5个及以上，且有50%失败的情况下，开启断路器；断路器开启1000ms后尝试关闭
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))  //必须
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(50)//超时时间
                        .withCircuitBreakerRequestVolumeThreshold(5)  //10s内最少的请求量，大于该值，断路器配置才会生效，默认20
                        .withCircuitBreakerSleepWindowInMilliseconds(1000) //短路器打开后多久尝试关闭(half open)
                        .withCircuitBreakerErrorThresholdPercentage(50)) //错误百分比，超过该值打开断路器
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ExampleGroup-pool"))  //可选,默认 使用 this.getClass().getSimpleName();
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(4)));
 
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
        }else if("Circuit".equals(name)){
            TimeUnit.MILLISECONDS.sleep(100);
        }
        System.out.println("业务正常运行中...................................");
 
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
