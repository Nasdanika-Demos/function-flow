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
import org.nasdanika.graph.processor.IncomingHandler;
import org.nasdanika.graph.processor.NodeProcessorConfig;
import org.nasdanika.graph.processor.ProcessorConfig;
import org.nasdanika.graph.processor.ProcessorInfo;

/**
 * This processor's is activated via hanler methods. 
 */
public class CarolProcessor {

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
	public CarolProcessor(
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

	@IncomingHandler	
	public Chat chat = new Chat() {

		@Override
		public Message chat(Message request) {
			System.out.println("[Carol in " + Thread.currentThread().getName() + "] Got this from Bob: " + request);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				return new Message(
						"Carol", 
						"Bob", 
						"Voce", 
						"Got interrupted, sorry!", 
						Thread.currentThread().getName(), 
						new Date(), 
						request, 
						null);		
			}
			return new Message(
					"Carol", 
					"Bob", 
					"Voice", 
					"I'm fine, say Hi back!", 
					Thread.currentThread().getName(), 
					new Date(), 
					request, 
					null);		
		}
		
	};

}
