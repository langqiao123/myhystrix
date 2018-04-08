package myhytrix;

import static org.junit.Assert.*;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.langqiao.myhytrix.CommandCollapserGetValueForKey;
import com.langqiao.myhytrix.CommandFallBack;
import com.langqiao.myhytrix.CommandHelloCircuit;
import com.langqiao.myhytrix.CommandHelloFailure;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

public class HystrixFallback {

	@Test
	public void test() {
		assertEquals("Hello Fallback",new CommandFallBack("World").execute());
	}
	
	@Test
	public void expTest() {
		assertEquals("Hello Failure Exception!", new CommandHelloFailure("Exception").execute());
	}

	@Test
	public void timeOutTest() {
	    assertEquals("Hello Failure Timeout!", new CommandHelloFailure("Timeout").execute());
	}
	
	@Test
	public void rejectTest() throws InterruptedException {
	    int count = 5;
	    while (count-- > 0){
	        new CommandHelloFailure("Reject").queue();
	        TimeUnit.MILLISECONDS.sleep(100);
	    }
	}
	
	@Test
	public void circuitTest() throws InterruptedException {
		int count = 6;
		while (count-- > 0){
			new CommandHelloCircuit("Circuit").queue();
			TimeUnit.MILLISECONDS.sleep(100);
		}
	}
	
	@Test
	public void testCollapser() throws Exception {
	    HystrixRequestContext context = HystrixRequestContext.initializeContext();
	    try {
	        Future<String> f1 = new CommandCollapserGetValueForKey(1).queue();
	        Future<String> f2 = new CommandCollapserGetValueForKey(2).queue();
	        Future<String> f3 = new CommandCollapserGetValueForKey(3).queue();
	        Future<String> f4 = new CommandCollapserGetValueForKey(4).queue();
	 
	 
	        assertEquals("ValueForKey: 1", f1.get());
	        assertEquals("ValueForKey: 2", f2.get());
	        assertEquals("ValueForKey: 3", f3.get());
	        assertEquals("ValueForKey: 4", f4.get());
	 
	        // assert that the batch command 'GetValueForKey' was in fact
	        // executed and that it executed only once
	        assertEquals(2, HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size());
	        HystrixCommand<?> command = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().toArray(new HystrixCommand<?>[1])[0];
	        // assert the command is the one we're expecting
	        assertEquals("GetValueForKey", command.getCommandKey().name());
	        // confirm that it was a COLLAPSED command execution
	        assertTrue(command.getExecutionEvents().contains(HystrixEventType.COLLAPSED));
	        // and that it was successful
	        assertTrue(command.getExecutionEvents().contains(HystrixEventType.SUCCESS));
	    } finally {
	        context.shutdown();
	    }
	}
}
