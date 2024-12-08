import java.util.concurrent.CompletionStage
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier

import org.nasdanika.capability.CapabilityFactory.Loader
import org.nasdanika.common.Invocable
import org.nasdanika.common.ProgressMonitor
import org.nasdanika.drawio.Node
import org.nasdanika.graph.Element
import org.nasdanika.graph.processor.OutgoingEndpoint
import org.nasdanika.graph.processor.ProcessorConfig
import org.nasdanika.graph.processor.ProcessorElement
import org.nasdanika.graph.processor.ProcessorInfo
import org.nasdanika.capability.CapabilityLoader
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.functionflow.processors.runtime.TransitionProcessor;

// Script arguments
CapabilityLoader capabilityLoader = args[0].getCapabilityLoader();
ProgressMonitor progressMonitor = args[1]
String fragment = args[2]
TransitionProcessor transitionProcessor = args[3]

new org.nasdanika.common.Invocable() {
	
	/**
	 * This method is invoked by the transition processor. 
	 */
	def invoke(Object... args) {
		String result = transitionProcessor.getTargetEndpoint().invoke(args) + " from Groovy";
		System.out.println(args + " -> " + result);
		return result;  
  	}
	
}


