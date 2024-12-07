package org.nasdanika.demos.functionflow;

import java.util.Date;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.nasdanika.capability.CapabilityFactory.Loader;
import org.nasdanika.common.Invocable;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.processor.ConnectionProcessorConfig;
import org.nasdanika.graph.processor.HandlerWrapper;
import org.nasdanika.graph.processor.IncomingHandler;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.graph.processor.OutgoingEndpoint;
import org.nasdanika.graph.processor.ProcessorConfig;
import org.nasdanika.graph.processor.ProcessorInfo;

/**
 * This processor's is activated via hanler methods. 
 */
public class BobProcessor {

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
	public BobProcessor(
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

	@IncomingHandler(wrap = HandlerWrapper.ASYNC_INVOCABLE)
	public Message chat(Message request) throws InterruptedException {
		System.out.println("[Bob in " + Thread.currentThread().getName() + "] Got this from Alice: " + request);
		Thread.sleep(200);
		
		if (request.text().contains("Carol")) {
			Message toCarol = new Message(
					"Bob", 
					"Carol", 
					"Voice", 
					"Hey Carol, Alice says Hi and asks how you are!", 
					Thread.currentThread().getName(), 
					new Date(), 
					null, 
					request);
			
			Message carolResponse = carolEndpoint.chat(toCarol);		
			return new Message(
					"Bob", 
					"Alice", 
					"SMS", 
					"She is fine, says Hi back!", 
					Thread.currentThread().getName(), 
					new Date(), 
					request, 
					carolResponse);					
		}
		
		return new Message(
				"Bob", 
				"Alice", 
				"SMS", 
				"I'm fine", 
				Thread.currentThread().getName(), 
				new Date(), 
				request, 
				null);		
	}
	
	@OutgoingEndpoint
	public Chat carolEndpoint;
	
}
