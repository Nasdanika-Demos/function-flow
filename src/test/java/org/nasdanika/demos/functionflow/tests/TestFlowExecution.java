package org.nasdanika.demos.functionflow.tests;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Test;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.capability.ServiceCapabilityFactory.Requirement;
import org.nasdanika.capability.emf.ResourceSetRequirement;
import org.nasdanika.common.Context;
import org.nasdanika.common.Invocable;
import org.nasdanika.common.PrintStreamProgressMonitor;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Transformer;
import org.nasdanika.graph.Connection;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.model.adapters.ElementAdapter;
import org.nasdanika.graph.model.adapters.GraphAdapterFactory;
import org.nasdanika.graph.processor.AsyncInvocableEndpointFactory;
import org.nasdanika.graph.processor.CapabilityProcessorFactory;
import org.nasdanika.graph.processor.HandlerType;
import org.nasdanika.graph.processor.ProcessorConfig;
import org.nasdanika.graph.processor.ProcessorConfigFactory;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.functionflow.End;
import org.nasdanika.models.functionflow.processors.runtime.FlowElementProcessor;

public class TestFlowExecution {
	
//	protected 
	
	@Test
	public void testFunctionFlowCapabilityProcessors() throws IOException {
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		Context context = Context.EMPTY_CONTEXT;
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
		
		Map<Element, ProcessorInfo<FlowElementProcessor>> processors = createProcessors("simple/flow.drawio", endResolver, context, progressMonitor);
		System.out.println(processors);
		
		//
//		// Root element processor
//		return processors
//				.entrySet()
//				.stream()
//				.filter(e -> e.getKey() instanceof NodeAdapter && ((NodeAdapter) e.getKey()).get() instanceof Start)
//				.map(Entry::getValue)
//				.findAny()
//				.get();
		
//		NodeProcessorInfo<Invocable, Invocable, Invocable> processorInfo = (NodeProcessorInfo<Invocable, Invocable, Invocable>) createCapabilityProcessor(processResource.getContents(), endResolver, context, progressMonitor);
//		Invocable processor = processorInfo.getProcessor();
//		Object result = processor.invoke("Hello");
//		System.out.println(result);
	}
		
	protected Map<org.nasdanika.graph.Element, ProcessorInfo<FlowElementProcessor>> createProcessors(
			String demoPath,
			Function<End,Invocable> endResolver, 
			Context context, 
			ProgressMonitor progressMonitor) throws IOException {
		
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
		ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
		File flowDiagramFile = new File(new File("demos"), demoPath).getCanonicalFile();
		Resource flowResource = resourceSet.getResource(URI.createFileURI(flowDiagramFile.getAbsolutePath()), true);
		
		// Creating adapters
		GraphAdapterFactory graphAdapterFactory = new GraphAdapterFactory();  
		Transformer<EObject,ElementAdapter<?>> graphFactory = new Transformer<>(graphAdapterFactory); 
		Map<EObject, ElementAdapter<?>> registry = graphFactory.transform(flowResource.getContents(), false, progressMonitor);
		
		AsyncInvocableEndpointFactory endpointFactory = new AsyncInvocableEndpointFactory(null);
		ProcessorConfigFactory<Object, Object> processorConfigFactory = new ProcessorConfigFactory<Object, Object>() {

			@Override
			public Object createEndpoint(Connection connection, Object handler, HandlerType type) {
				return endpointFactory.createEndpoint(connection, handler, type);
			}
			
			@Override
			protected boolean isPassThrough(Connection connection) {
				// TODO Auto-generated method stub
				return super.isPassThrough(connection);
			}
			
		};
		
		Transformer<org.nasdanika.graph.Element, ProcessorConfig> transformer = new Transformer<>(processorConfigFactory);
		Map<org.nasdanika.graph.Element, ProcessorConfig> configs = transformer.transform(registry.values(), false, progressMonitor);

		CapabilityProcessorFactory<Object, FlowElementProcessor> processorFactory = new CapabilityProcessorFactory<>(
				FlowElementProcessor.class, 
				Invocable.class, 
				Invocable.class, 
				endResolver, 
				capabilityLoader); 
		
		Map<org.nasdanika.graph.Element, ProcessorInfo<FlowElementProcessor>> processors = processorFactory.createProcessors(configs.values(), false, progressMonitor);
		
		return processors;
	}
	
}
