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
package com.dragome.serverside.servlets;

import ro.isdc.wro.manager.factory.BaseWroManagerFactory;
import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.locator.ClasspathUriLocator;
import ro.isdc.wro.model.resource.locator.ServletContextUriLocator;
import ro.isdc.wro.model.resource.locator.UrlUriLocator;
import ro.isdc.wro.model.resource.locator.factory.SimpleUriLocatorFactory;
import ro.isdc.wro.model.resource.locator.factory.UriLocatorFactory;
import ro.isdc.wro.model.resource.processor.factory.ProcessorsFactory;
import ro.isdc.wro.model.resource.processor.factory.SimpleProcessorsFactory;
import ro.isdc.wro.model.resource.processor.impl.css.CssCompressorProcessor;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;

public class DragomeWroManagerFactory extends BaseWroManagerFactory
{
	protected UriLocatorFactory newUriLocatorFactory()
	{
		SimpleUriLocatorFactory uriLocatorFactory= (SimpleUriLocatorFactory) super.newUriLocatorFactory();
		uriLocatorFactory.addLocator(new ClasspathUriLocator());
		uriLocatorFactory.addLocator(new ServletContextUriLocator());
		uriLocatorFactory.addLocator(new UrlUriLocator());
		return uriLocatorFactory;
	}
	protected ProcessorsFactory newProcessorsFactory()
	{
		final SimpleProcessorsFactory processorsFactory= (SimpleProcessorsFactory) super.newProcessorsFactory();
		processorsFactory.addPreProcessor(new CssCompressorProcessor());
		processorsFactory.addPreProcessor(new JSMinProcessor());
		return processorsFactory;
	}
	protected WroModelFactory newModelFactory()
	{
		return new WroModelFactory()
		{
			public WroModel create()
			{
				Group dragomeGroup= new Group("dragome");
				dragomeGroup.addResource(Resource.create("/dragome-resources/js/jquery.js", ResourceType.JS)); //TODO extraer constantes
				dragomeGroup.addResource(Resource.create("/dragome-resources/js/hashtable.js", ResourceType.JS));
				dragomeGroup.addResource(Resource.create("/dragome-resources/js/deflate.js", ResourceType.JS));
				dragomeGroup.addResource(Resource.create("/dragome-resources/js/helpers.js", ResourceType.JS));
				dragomeGroup.addResource(Resource.create("/dragome-resources/js/String.js", ResourceType.JS));
				dragomeGroup.addResource(Resource.create("/dragome-resources/js/jquery.atmosphere.js", ResourceType.JS));
				dragomeGroup.addResource(Resource.create("/dragome-resources/js/application.js", ResourceType.JS));
				dragomeGroup.addResource(Resource.create("/dragome-resources/js/q-3.0.js", ResourceType.JS));
				dragomeGroup.addResource(Resource.create("/compiled-js/webapp.js", ResourceType.JS));
				dragomeGroup.addResource(Resource.create("/dragome-resources/css/dragome.css", ResourceType.CSS));

				Group compiledGroup= new Group("compiled");
				compiledGroup.addResource(Resource.create("/compiled-js/webapp.js", ResourceType.JS));

				WroModel wroModel= new WroModel();
				wroModel.addGroup(dragomeGroup);
				wroModel.addGroup(compiledGroup);

				return wroModel;
			}

			public void destroy()
			{
			}
		};
	}
}
