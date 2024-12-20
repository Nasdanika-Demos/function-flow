package org.nasdanika.demos.functionflow.tests;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
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
import org.nasdanika.models.functionflow.Flow;
import org.nasdanika.models.functionflow.processors.runtime.ExceptionHandler;
import org.nasdanika.models.functionflow.processors.runtime.ExecutionListener;
import org.nasdanika.models.functionflow.processors.runtime.FlowElementProcessor;
import org.nasdanika.models.functionflow.processors.runtime.FlowProcessor;

public class TestFlowExecutionBase {
		
	protected Map<org.nasdanika.graph.Element, ProcessorInfo<FlowElementProcessor<EObject>>> createProcessors(
			String demoPath,
			Executor executor,
			Context context, 
			ProgressMonitor progressMonitor) throws IOException {
		
		System.out.println("=== Loading ===");
		
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
		ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
		File flowDiagramFile = new File(new File("demos"), demoPath).getCanonicalFile();
		Resource flowResource = resourceSet.getResource(URI.createFileURI(flowDiagramFile.getAbsolutePath()), true);
		
		// Creating adapters
		GraphAdapterFactory graphAdapterFactory = new GraphAdapterFactory();  
		Transformer<EObject,ElementAdapter<?>> graphFactory = new Transformer<>(graphAdapterFactory); 
		Map<EObject, ElementAdapter<?>> registry = graphFactory.transform(flowResource.getContents(), false, progressMonitor);
		
		AsyncInvocableEndpointFactory endpointFactory = new AsyncInvocableEndpointFactory(executor);
		ProcessorConfigFactory<Object, Object> processorConfigFactory = new ProcessorConfigFactory<Object, Object>() {
	
			@Override
			public Object createEndpoint(Connection connection, Object handler, HandlerType type) {
				return endpointFactory.createEndpoint(connection, handler, type);
			}
			
			@Override
			protected boolean isPassThrough(Connection connection) {
				return false;
			}
			
		};
		
		Transformer<org.nasdanika.graph.Element, ProcessorConfig> transformer = new Transformer<>(processorConfigFactory);
		Map<org.nasdanika.graph.Element, ProcessorConfig> configs = transformer.transform(registry.values(), false, progressMonitor);
	
		CapabilityProcessorFactory<Object, FlowElementProcessor<EObject>> processorFactory = new CapabilityProcessorFactory<>(
				FlowElementProcessor.class, 
				Invocable.class, 
				Invocable.class, 
				null, 
				capabilityLoader); 
		
		return processorFactory.createProcessors(configs.values(), false, progressMonitor);
	}	
	
	protected void execute(
		String path,	
		Invocable target, 
		ExceptionHandler exceptionHandler,
		ExecutionListener executionLister,
		Consumer<FlowProcessor<Flow>> operator) throws IOException, InterruptedException {
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		Context context = Context.EMPTY_CONTEXT;
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		
		Map<Element, ProcessorInfo<FlowElementProcessor<EObject>>> processors = createProcessors(
				path, 
				executor,
				context, 
				progressMonitor);
		processors.forEach((k,v) -> System.out.println(k + " -> " + v.getProcessor()));
		
		@SuppressWarnings("unchecked")
		FlowProcessor<Flow> flowProcessor = processors
			.values()
			.stream()
			.map(ProcessorInfo::getProcessor)
			.filter(Objects::nonNull)
			.filter(p -> p.parentProcessor == null)
			.filter(FlowProcessor.class::isInstance)
			.map(FlowProcessor.class::cast)
			.findAny()
			.get();
		
		flowProcessor.setTarget(target);		
		flowProcessor.setExceptionHandler(exceptionHandler);
		flowProcessor.setExecutionListener(executionLister);
		
		// Starting
		System.out.println("=== Strting ===");
		flowProcessor.start(progressMonitor);
		
		System.out.println("=== Executing ===");		
		operator.accept(flowProcessor);
		
		// Stopping
		System.out.println("=== Stopping ===");
		flowProcessor.stop(progressMonitor);
		
		// Shutting down executor
		System.out.println("=== Shutting down executor ===");
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);

		// Closing
		System.out.println("=== Closing ===");
		flowProcessor.close(progressMonitor);
	}
	
}
