package org.nasdanika.demos.functionflow.tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.capability.ServiceCapabilityFactory.Requirement;
import org.nasdanika.capability.emf.ResourceSetRequirement;
import org.nasdanika.common.Context;
import org.nasdanika.common.Diagnostic;
import org.nasdanika.common.ExecutionException;
import org.nasdanika.common.MutableContext;
import org.nasdanika.common.PrintStreamProgressMonitor;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.models.app.Action;
import org.nasdanika.models.app.gen.AppSiteGenerator;
import org.nasdanika.models.ecore.graph.processors.EcoreHtmlAppGenerator;

public class TestFunctionFlowDemoSiteGen {
	
	private static File generateHtmlAppModel(
			String path, 
			ResourceSet resourceSet,
			File demosDir,
			File flowModelsDir, 
			File actionModelsDir,
			ProgressMonitor progressMonitor) throws IOException  {
		
		File flowFile = new File(demosDir, path).getCanonicalFile();
		Resource flowResource = resourceSet.getResource(URI.createFileURI(flowFile.getAbsolutePath()), true);
		
		Resource flowResourceDump = resourceSet.createResource(URI.createFileURI(new File(flowModelsDir + "/" + path + ".xmi").getAbsolutePath()));
		flowResourceDump.getContents().addAll(EcoreUtil.copyAll(flowResource.getContents()));
		flowResourceDump.save(null);
		
		// Generating an action model
		MutableContext context = Context.EMPTY_CONTEXT.fork();
		Consumer<Diagnostic> diagnosticConsumer = d -> d.dump(System.out, 0);		
		File output = new File(actionModelsDir, path + ".xmi");
		
		EcoreHtmlAppGenerator htmlAppGenerator = EcoreHtmlAppGenerator.loadEcoreHtmlAppGenerator(
				flowResource.getContents(), 
				context,
				null, // java.util.function.BiFunction<URI, ProgressMonitor, Action> prototypeProvider,			
				null, // Predicate<Object> factoryPredicate,
				null, // Predicate<EPackage> ePackagePredicate,
				diagnosticConsumer,
				progressMonitor);
		
		Resource actionModelResource = htmlAppGenerator.generateHtmlAppModel(
				diagnosticConsumer, 
				output,
				progressMonitor);
		
		if (actionModelResource.getContents().size() == 1) {
			EObject root = actionModelResource.getContents().get(0);
			if (root instanceof Action) {
				((Action) root).setLocation(path.replace("\\", "/") + "/index.html");
				actionModelResource.save(null);
			}
		}

		return output;
	}
		
	@Test
	public void testGenerateFunctionFlowDemoSite() throws Exception {
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
		ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
		
		File demosDir = new File("demos");
		
		File actionModelsDir = new File("target\\action-models\\");
		actionModelsDir.mkdirs();
		
		File flowModelsDir = new File("target\\flow-models\\");
		flowModelsDir.mkdirs();
				
		generateHtmlAppModel("simple\\flow.drawio", resourceSet, demosDir, flowModelsDir, actionModelsDir, progressMonitor);
		generateHtmlAppModel("groovy-transition\\flow.drawio", resourceSet, demosDir, flowModelsDir, actionModelsDir, progressMonitor);
		generateHtmlAppModel("java-transition\\flow.drawio", resourceSet, demosDir, flowModelsDir, actionModelsDir, progressMonitor);
		
		// Generating a web site
		String rootActionResource = "actions.yml";
		URI rootActionURI = URI.createFileURI(new File(rootActionResource).getAbsolutePath());//.appendFragment("/");
		
		String pageTemplateResource = "page-template.yml";
		URI pageTemplateURI = URI.createFileURI(new File(pageTemplateResource).getAbsolutePath());//.appendFragment("/");
		
		String siteMapDomain = "https://nasdanika-demos.github.io/function-flow/";		
		AppSiteGenerator actionSiteGenerator = new AppSiteGenerator() {
			
			protected boolean isDeleteOutputPath(String path) {
				return !"CNAME".equals(path);				
			};
			
		};		
		
		Map<String, Collection<String>> errors = actionSiteGenerator.generate(
				rootActionURI, 
				pageTemplateURI, 
				siteMapDomain, 
				new File("docs"), // Publishing to the repository's docs directory for GitHub pages 
				new File("target/ibs-doc-site-work-dir"), 
				true);
		
		int errorCount = 0;
		for (Entry<String, Collection<String>> ee: errors.entrySet()) {
			System.err.println(ee.getKey());
			for (String error: ee.getValue()) {
				System.err.println("\t" + error);
				++errorCount;
			}
		}
		
		System.out.println("There are " + errorCount + " site errors");
		
		if (errorCount != 20) {
			throw new ExecutionException("There are problems with pages: " + errorCount);
		}		
		
	}	
		
}
