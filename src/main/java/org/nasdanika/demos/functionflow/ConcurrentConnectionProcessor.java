package org.nasdanika.demos.functionflow;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.nasdanika.capability.CapabilityFactory.Loader;
import org.nasdanika.common.Invocable;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.Connection;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.processor.AsyncInvocableConnectionProcessor;
import org.nasdanika.graph.processor.ParentProcessor;
import org.nasdanika.graph.processor.ProcessorConfig;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.graph.processor.RegistryEntry;

public class ConcurrentConnectionProcessor extends AsyncInvocableConnectionProcessor {

	public ConcurrentConnectionProcessor(
			Loader loader, 
			ProgressMonitor loaderProgressMonitor, 
			Object data,
			String fragment, 
			ProcessorConfig config,
			BiConsumer<Element, BiConsumer<ProcessorInfo<Invocable>, ProgressMonitor>> infoProvider,
			Consumer<CompletionStage<?>> endpointWiringStageConsumer, ProgressMonitor wiringProgressMonitor) {
		super(
				loader, 
				loaderProgressMonitor, 
				data, 
				fragment, 
				config, 
				infoProvider, 
				endpointWiringStageConsumer,
				wiringProgressMonitor);
	}
	
	@ParentProcessor
//	@RegistryEntry("#this == #element.parent")
	public void setThreadPool(Supplier<Executor> threadPoolSupplier) {
		executor = threadPoolSupplier.get();
	}

}
