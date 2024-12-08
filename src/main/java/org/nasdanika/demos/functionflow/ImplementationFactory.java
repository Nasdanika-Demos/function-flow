package org.nasdanika.demos.functionflow;

import org.nasdanika.capability.CapabilityFactory.Loader;
import org.nasdanika.common.Invocable;
import org.nasdanika.common.InvocableComponent;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.functionflow.Transition;
import org.nasdanika.models.functionflow.processors.runtime.TransitionProcessor;

/**
 * Provides static method to create flow element implementations  
 */
public class ImplementationFactory {
	
	private ImplementationFactory() {
		// Utility
	}
	
	/**
	 * This is the factory method signature for flow element implementations factory methods
	 * @param loader
	 * @param progressMonitor
	 * @param data
	 * @param fragment
	 * @param transitionProcessor
	 * @return
	 */
	public static Invocable crateTransitionImplementation(
			Loader loader,
			ProgressMonitor progressMonitor,
			Object data,
			String fragment,
			TransitionProcessor<Transition> transitionProcessor) {
		
		return new InvocableComponent() {
			
			@Override
			public void stop(ProgressMonitor progressMonitor) {
				System.out.println("Stopping: " + this);
			}
			
			@Override
			public void start(ProgressMonitor progressMonitor) {
				System.out.println("Starting: " + this);
			}
			
			@Override
			public void close(ProgressMonitor progressMonitor) {
				System.out.println("Closing: " + this);
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public String invoke(Object... args) {
				return transitionProcessor.getTargetEndpoint().invoke(args) + " from Java";
			}
		};
	}

}
