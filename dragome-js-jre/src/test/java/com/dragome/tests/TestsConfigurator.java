package com.dragome.tests;

import java.net.URL;

import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.web.config.DomHandlerApplicationConfigurator;

@DragomeConfiguratorImplementor(priority = 1)
public class TestsConfigurator extends DomHandlerApplicationConfigurator {
	public TestsConfigurator() {
		super();
	}

	public boolean isRemoveUnusedCode() {
		return true;
	}

	public URL getAdditionalCodeKeepConfigFile() {
		return getClass().getResource("/proguard-extra.conf");
	}

	public boolean filterClassPath(String classpathEntry) {
		boolean include = super.filterClassPath(classpathEntry);
		include |= classpathEntry.contains("junit-4");
		include |= classpathEntry.contains("hamcrest-core");

		return include;
	}
}
