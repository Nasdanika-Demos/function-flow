package org.nasdanika.demos.functionflow;

import java.util.concurrent.CompletableFuture;

public interface AsyncChat {
			
	CompletableFuture<Message> chat(Message request); 

}
