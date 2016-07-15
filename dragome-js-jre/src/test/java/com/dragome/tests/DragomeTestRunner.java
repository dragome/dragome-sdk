package com.dragome.tests;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;

import com.dragome.services.ServiceLocator;
import com.dragome.services.serverside.ServerReflectionServiceImpl;
import com.dragome.web.helpers.serverside.StandaloneDragomeAppGenerator;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DragomeTestRunner extends ParentRunner<FrameworkMethod> {
	private Class<?> clazz;
	private HtmlPage page;
	protected boolean failed;

	public DragomeTestRunner(Class<?> aClass) throws Exception {
		super(aClass);
		this.clazz = aClass;

		ServiceLocator serviceLocator= ServiceLocator.getInstance();
		serviceLocator.setReflectionService(new ServerReflectionServiceImpl());
			serviceLocator.setConfigurator(new TestsConfigurator());

		
		File destinationDirectory = new File("./target/dragome-tests");
		destinationDirectory.mkdirs();
		File webappDirectory = new File("./src/test/resources");
		new StandaloneDragomeAppGenerator(destinationDirectory, webappDirectory, true, true).execute();

		Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		page = webClient.getPage(new File(destinationDirectory, "tests.html").toURI().toURL());
	}

	protected List<FrameworkMethod> getChildren() {
		List<FrameworkMethod> result = new ArrayList<>();
		for (Method method : clazz.getMethods()) {
			if (method.getName().startsWith("test")) {
				FrameworkMethod frameworkMethod = new FrameworkMethod(method);
				result.add(frameworkMethod);
			}
		}

		return result;
	}

	protected Description describeChild(FrameworkMethod child) {
		return Description.createTestDescription(clazz, child.getName());
	}

	protected void runChild(FrameworkMethod child, RunNotifier notifier) {
		Description spec1 = Description.createTestDescription(clazz, child.getName());

		String sourceCode = "try{new " + javaToDragomeNotation(clazz) + "()."
				+ javaMethodNameToDragomeNotation(child.getName())
				+ "(); window.failed=false}catch(e){window.failed=true}";
		ScriptResult executeJavaScript = page.executeJavaScript(sourceCode);

		String resultSource = "window.failed";
		executeJavaScript = page.executeJavaScript(resultSource);

		notifier.fireTestStarted(spec1);
		if (executeJavaScript.getJavaScriptResult().toString().equals("true"))
			notifier.fireTestFailure(new Failure(spec1, new Exception("Test failed in javascript engine")));
		else
			notifier.fireTestFinished(spec1);

		failed = false;
	}

	private String javaMethodNameToDragomeNotation(String name) {
		String string = name.replaceAll("\\[\\]", "_ARRAYTYPE");
		String result = string.replaceAll("\\(\\)", "\\$");
		result = result.replaceAll("\\)", "\\$").replaceAll("\\(", "___").replaceAll("\\.", "_").replaceAll(",", "__")
				.replaceAll("<", "").replaceAll(">", "").replaceAll("\\[", "_").replaceAll("\\]", "_")
				.replaceAll(";", "\\$");

		result = "$" + result;

		result = result.replaceAll("___$", "");
		if (result.contains("clinit"))
			result = "$" + result + "_";

		result = result + "$void";
		return result;
	}

	private String javaToDragomeNotation(Class<?> aClazz) {
		return aClazz.getName().replace(".", "_");
	}
}