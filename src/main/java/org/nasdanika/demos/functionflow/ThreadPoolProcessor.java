package org.nasdanika.demos.functionflow;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.nasdanika.capability.CapabilityFactory.Loader;
import org.nasdanika.common.Invocable;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Util;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.processor.ProcessorConfig;
import org.nasdanika.graph.processor.ProcessorInfo;

/**
 * This processor's is activated via hanler methods. 
 */
public class ThreadPoolProcessor implements AutoCloseable, Supplier<Executor> {
	
	protected Executor executor;
	private boolean shutdownExecutor;
	protected long terminationTimeout;
	protected TimeUnit terminationTimeoutUnit;
	
	public ThreadPoolProcessor(
			Loader loader,
			ProgressMonitor loaderProgressMonitor,
			Object data,
			String fragment,
			ProcessorConfig config,
			BiConsumer<Element, BiConsumer<ProcessorInfo<Invocable>, ProgressMonitor>> infoProvider,
			Consumer<CompletionStage<?>> endpointWiringStageConsumer,
			ProgressMonitor wiringProgressMonitor) {

		if (!Util.isBlank(fragment)) {
			this.executor = Executors.newFixedThreadPool(Integer.parseInt(fragment));
			this.terminationTimeout = 1;
			this.terminationTimeoutUnit = TimeUnit.MINUTES;
			shutdownExecutor = true;
			
		}
	}	

	@Override
	public void close() throws Exception {
		if (shutdownExecutor && executor instanceof ExecutorService) {
			ExecutorService executorService = (ExecutorService) executor;
			executorService.shutdown();
			executorService.awaitTermination(terminationTimeout, terminationTimeoutUnit);
		}		
	}

	@Override
	public Executor get() {
		return executor;
	}
	
}