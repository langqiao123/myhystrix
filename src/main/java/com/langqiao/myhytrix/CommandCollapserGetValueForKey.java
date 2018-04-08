package com.langqiao.myhytrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

public class CommandCollapserGetValueForKey extends HystrixCollapser<List<String>, String, Integer> {
	 
    private final Integer key;
 
    public CommandCollapserGetValueForKey(Integer key) {
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("Collapser"))
                .andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter()
                        .withMaxRequestsInBatch(3)		//	每个批次最大的请求数，超过该值，创建新的batch请求
                .withTimerDelayInMilliseconds(10)));
        this.key = key;
    }
 
    @Override
    public Integer getRequestArgument() {
        return key;
    }
 
    @Override
    protected HystrixCommand<List<String>> createCommand(final Collection<CollapsedRequest<String, Integer>> requests) {
        return new BatchCommand(requests);
    }
 
    @Override
    protected void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, Integer>> requests) {
        int count = 0;
        for (CollapsedRequest<String, Integer> request : requests) {
            request.setResponse(batchResponse.get(count++));
        }
    }
 
    private static final class BatchCommand extends HystrixCommand<List<String>> {
        private final Collection<CollapsedRequest<String, Integer>> requests;
 
        private BatchCommand(Collection<CollapsedRequest<String, Integer>> requests) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("GetValueForKey")));
            this.requests = requests;
        }
 
        @Override
        protected List<String> run() {
            System.out.println("BatchCommand run  "+requests.size());
            ArrayList<String> response = new ArrayList<String>();
            for (CollapsedRequest<String, Integer> request : requests) {
                // artificial response for each argument received in the batch
                response.add("ValueForKey: " + request.getArgument());
            }
            return response;
        }
    }
}