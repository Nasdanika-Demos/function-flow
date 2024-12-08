package org.nasdanika.demos.functionflow.tests;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.Test;
import org.nasdanika.common.Invocable;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.model.adapters.NodeAdapter;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.functionflow.End;
import org.nasdanika.models.functionflow.Start;
import org.nasdanika.models.functionflow.processors.runtime.FlowElementProcessor;

public class TestFlowExecution extends TestFlowExecutionBase {
	
	@Test
	public void testSimple() throws IOException, InterruptedException {
		Function<End, Invocable> endResolver = end -> {
			return new Invocable() {
				
				@SuppressWarnings("unchecked")
				@Override
				public <T> T invoke(Object... args) {
					System.out.println("End invoked: " + end + " " + args);
					return (T) "Purum";
				}
			};
		};
		execute("simple/flow.drawio", endResolver, this::onSimple); 
	}
	
	protected void onSimple(Map<Element, ProcessorInfo<FlowElementProcessor<EObject>>> processors) {
				
		// Start processor
		FlowElementProcessor<?> startProcessor = processors
				.entrySet()
				.stream()
				.filter(e -> e.getKey() instanceof NodeAdapter && ((NodeAdapter) e.getKey()).get() instanceof Start)
				.map(Entry::getValue)
				.map(ProcessorInfo::getProcessor)
				.findAny()
				.get();
		
		Map<Object, CompletableFuture<?>> result = startProcessor.invoke("Hello");
		result.values().forEach(cp -> {
			System.out.println(cp);
			cp.whenComplete((r, e) -> System.out.println(">>> " + r + " " + e));	
		});
	}
		
	@Test
	public void testGroovyTransition() throws IOException, InterruptedException {
		Function<End, Invocable> endResolver = end -> {
			return new Invocable() {
				
				@SuppressWarnings("unchecked")
				@Override
				public <T> T invoke(Object... args) {
					System.out.println("End invoked: " + end + " " + args);
					return (T) "Purum";
				}
			};
		};
		execute("groovy-transition/flow.drawio", endResolver, this::onGroovyTransition); 
	}
	
	protected void onGroovyTransition(Map<Element, ProcessorInfo<FlowElementProcessor<EObject>>> processors) {
				
		// Start processor
		FlowElementProcessor<?> startProcessor = processors
				.entrySet()
				.stream()
				.filter(e -> e.getKey() instanceof NodeAdapter && ((NodeAdapter) e.getKey()).get() instanceof Start)
				.map(Entry::getValue)
				.map(ProcessorInfo::getProcessor)
				.findAny()
				.get();
		
		Map<Object, CompletableFuture<?>> result = startProcessor.invoke("Hello");
		result.values().forEach(cp -> {
			System.out.println(cp);
			cp.whenComplete((r, e) -> System.out.println(">>> " + r + " === " + e));	
		});
	}
	
}
