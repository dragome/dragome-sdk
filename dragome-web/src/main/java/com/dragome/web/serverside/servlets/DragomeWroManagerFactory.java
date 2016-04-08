/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 *  This file is part of Dragome SDK.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.web.serverside.servlets;

import java.io.File;
import java.util.List;

import ro.isdc.wro.manager.factory.BaseWroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.resource.locator.ClasspathUriLocator;
import ro.isdc.wro.model.resource.locator.ServletContextUriLocator;
import ro.isdc.wro.model.resource.locator.UrlUriLocator;
import ro.isdc.wro.model.resource.locator.factory.SimpleUriLocatorFactory;
import ro.isdc.wro.model.resource.locator.factory.UriLocatorFactory;
import ro.isdc.wro.model.resource.locator.support.LocatorProvider;
import ro.isdc.wro.model.resource.processor.factory.DefaultProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.SimpleProcessorsFactory;
import ro.isdc.wro.model.resource.processor.impl.css.CssCompressorProcessor;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;
import ro.isdc.wro.util.provider.ProviderFinder;

public class DragomeWroManagerFactory extends BaseWroManagerFactory
{
	private File baseFolder;

	public DragomeWroManagerFactory()
	{
	}

	public DragomeWroManagerFactory(File baseFolder)
	{
		super();
		this.baseFolder= baseFolder;
	}

	protected UriLocatorFactory newUriLocatorFactory()
	{
		SimpleUriLocatorFactory uriLocatorFactory= new SimpleUriLocatorFactory();
		if (baseFolder != null)
			uriLocatorFactory.addLocator(new FileSystemUriLocator(baseFolder));

		final List<LocatorProvider> providers= ProviderFinder.of(LocatorProvider.class).find();
		for (final LocatorProvider provider : providers)
			uriLocatorFactory.addLocators(provider.provideLocators().values());

		uriLocatorFactory.addLocator(new ClasspathUriLocator());
		uriLocatorFactory.addLocator(new ServletContextUriLocator());
		uriLocatorFactory.addLocator(new UrlUriLocator());
		return uriLocatorFactory;
	}
	protected ProcessorsFactory newProcessorsFactory()
	{
		return createProcessorFactory();
	}

	public static ProcessorsFactory createProcessorFactory()
	{
		final SimpleProcessorsFactory processorsFactory= new DefaultProcessorsFactory();
		processorsFactory.addPreProcessor(new CssCompressorProcessor());
		processorsFactory.addPreProcessor(new JSMinProcessor());
		return processorsFactory;
	}
	protected WroModelFactory newModelFactory()
	{
		return new DragomeWroModelFactory();
	}
}
