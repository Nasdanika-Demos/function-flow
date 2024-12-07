package org.nasdanika.demos.functionflow;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.nasdanika.capability.CapabilityFactory.Loader;
import org.nasdanika.common.Invocable;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.processor.ConnectionProcessorConfig;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.graph.processor.OutgoingEndpoint;
import org.nasdanika.graph.processor.ProcessorConfig;
import org.nasdanika.graph.processor.ProcessorInfo;

/**
 * This processor's is invoked from a dynamic proxy apply(). 
 */
public class AliceProcessor implements Invocable {

	/**
	 * This is the constructor signature for graph processor classes which are to e instantiated by URIInvocableCapabilityFactory (org.nasdanika.capability.factories.URIInvocableCapabilityFactory).
	 * Config may be of specific types {@link ProcessorConfig} - {@link NodeProcessorConfig} or {@link ConnectionProcessorConfig}.  
	 * @param loader
	 * @param loaderProgressMonitor
	 * @param data
	 * @param fragment
	 * @param config
	 * @param infoProvider
	 * @param endpointWiringStageConsumer
	 * @param wiringProgressMonitor
	 */
	public AliceProcessor(
			Loader loader,
			ProgressMonitor loaderProgressMonitor,
			Object data,
			String fragment,
			ProcessorConfig config,
			BiConsumer<Element, BiConsumer<ProcessorInfo<Invocable>, ProgressMonitor>> infoProvider,
			Consumer<CompletionStage<?>> endpointWiringStageConsumer,
			ProgressMonitor wiringProgressMonitor) {
		
		System.out.println("I got constructed " + this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String invoke(Object... args) {
		Message m1 = new Message(
				"Alice", 
				"Bob", 
				"SMS", 
				"Hi, Bob! How are you?", 
				Thread.currentThread().getName(), 
				new Date(), 
				null, 
				null);
		
		CompletableFuture<Message> responseCF1 = bobEndpoint.chat(m1);		
		responseCF1.thenAccept(response -> {
			System.out.println("[" + Thread.currentThread().getName() + "] Response: " + response);			
		});
		
		Message m2 = new Message(
				"Alice", 
				"Bob", 
				"SMS", 
				"By the way, say Hi to Carol! How is she doing?", 
				Thread.currentThread().getName(), 
				new Date(), 
				null, 
				null);
		
		
		CompletableFuture<Message> responseCF2 = bobEndpoint.chat(m2);				
		return responseCF2.join().toString();
	}
	
	@OutgoingEndpoint
	public AsyncChat bobEndpoint;

}
