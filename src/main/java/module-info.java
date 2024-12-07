module org.nasdanika.demos.functionflow {
		
	requires transitive org.nasdanika.models.functionflow.processors.doc;
	requires org.nasdanika.models.functionflow.processors.runtime;
		
	opens org.nasdanika.demos.functionflow to org.nasdanika.common;
	
}
