package org.nasdanika.demos.functionflow.tests;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.nasdanika.common.Invocable;
import org.nasdanika.graph.Connection;
import org.nasdanika.models.functionflow.Flow;
import org.nasdanika.models.functionflow.processors.runtime.ExceptionHandler;
import org.nasdanika.models.functionflow.processors.runtime.ExecutionListener;
import org.nasdanika.models.functionflow.processors.runtime.FlowElementProcessor;
import org.nasdanika.models.functionflow.processors.runtime.FlowProcessor;

public class TestFlowExecution extends TestFlowExecutionBase {

	private Invocable target = new Invocable() {
		
		@SuppressWarnings("unchecked")
		@Override
		public <T> T invoke(Object... args) {
			System.out.println("[" + Thread.currentThread().getName() + "] Target invoked: " + Arrays.toString(args));
			return (T) "Purum";
		}
	};
	
	private ExceptionHandler exceptionHandler = new ExceptionHandler() {
		
		@Override
		public <T> T handleException(FlowElementProcessor<?> processor, Connection activator, Object[] args, RuntimeException exception) {
			System.err.println("Exception in " + processor + ", args: " + args + ", exception: " + exception);
			exception.printStackTrace();
			throw exception ;
		}
		
	};
	
	private ExecutionListener executionListener = new ExecutionListener() {
		
		@Override
		public synchronized void onInvoke(
				Instant start, 
				Instant end, 
				FlowElementProcessor<?> processor, 
				Connection activator,
				Object[] args, 
				Object result, 
				RuntimeException exception) {
			
			System.out.println("=== Invocation ===");
			System.out.println("\tThread: " + Thread.currentThread().getName());
			System.out.println("\tStart: " + start);
			System.out.println("\tEnd: " + end);
			System.out.println("\tProcessor: " + processor);
			System.out.println("\tActivator: " + activator);
			System.out.println("\tArgs: " + Arrays.toString(args));
			System.out.println("\tResult: " + result);
			System.out.println("\tException: " + exception);
			
		}
	};
		
	@Test
	public void testTransition() throws IOException, InterruptedException {
		execute("transition/flow.drawio", target, exceptionHandler, executionListener, this::onTransition); 
	}
	
	protected void onTransition(FlowProcessor<Flow> flowProcessor) {
		Map<Object, Map<?,CompletableFuture<?>>> result = flowProcessor.invoke("Hello");
		result.values().stream().flatMap(m -> m.values().stream()).forEach(cp -> {
			System.out.println(cp);
			cp.whenComplete((r, e) -> System.out.println(">>> " + r + " === " + e));	
		});
	}
		
	@Test
	public void testGroovyTransition() throws IOException, InterruptedException {
		execute("groovy-transition/flow.drawio", target, exceptionHandler, executionListener, this::onGroovyTransition); 
	}
	
	protected void onGroovyTransition(FlowProcessor<Flow> flowProcessor) {
		Map<Object, Map<?,CompletableFuture<?>>> result = flowProcessor.invoke("Hello");
		result.values().stream().flatMap(m -> m.values().stream()).forEach(cp -> {
			System.out.println(cp);
			cp.whenComplete((r, e) -> System.out.println(">>> " + r + " === " + e));	
		});
	}
	
	@Test
	public void testJavaTransition() throws IOException, InterruptedException {
		execute("java-transition/flow.drawio", target, exceptionHandler, executionListener, this::onJavaTransition); 
	}
	
	protected void onJavaTransition(FlowProcessor<Flow> flowProcessor) {
		Map<Object, Map<?,CompletableFuture<?>>> result = flowProcessor.invoke("Hello");
		result.values().stream().flatMap(m -> m.values().stream()).forEach(cp -> {
			System.out.println(cp);
			cp.whenComplete((r, e) -> System.out.println(">>> " + r + " === " + e));	
		});
	}
	
	@Test
	public void testCall() throws IOException, InterruptedException {
		execute("call/flow.drawio", target, exceptionHandler, executionListener, this::onCall); 
	}
	
	protected void onCall(FlowProcessor<Flow> flowProcessor) {
		Map<Object, Object> result = flowProcessor.invoke("Hello");
		result.values().forEach(System.out::println);
	}
	
}
