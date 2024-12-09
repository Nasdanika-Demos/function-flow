package org.nasdanika.demos.functionflow.tests;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.nasdanika.common.Invocable;
import org.nasdanika.models.functionflow.Flow;
import org.nasdanika.models.functionflow.processors.runtime.FlowProcessor;

public class TestFlowExecution extends TestFlowExecutionBase {

	private Invocable target = new Invocable() {
		
		@SuppressWarnings("unchecked")
		@Override
		public <T> T invoke(Object... args) {
			System.out.println("[" + Thread.currentThread().getName() + "] Target invoked: " + args);
			return (T) "Purum";
		}
	};
	
	
	@Test
	public void testTransition() throws IOException, InterruptedException {
		execute("transition/flow.drawio", target, this::onTransition); 
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
		execute("groovy-transition/flow.drawio", target, this::onGroovyTransition); 
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
		execute("java-transition/flow.drawio", target, this::onJavaTransition); 
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
		execute("call/flow.drawio", target, this::onCall); 
	}
	
	protected void onCall(FlowProcessor<Flow> flowProcessor) {
		Map<Object, Object> result = flowProcessor.invoke("Hello");
		result.values().forEach(System.out::println);
	}
	
}
