/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.guia;

import com.dragome.guia.events.listeners.interfaces.StyleChangedListener;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.services.ServiceLocator;
import com.dragome.services.interfaces.ReflectionService;
import com.dragome.templates.interfaces.TemplateListener;
import com.dragome.templates.interfaces.TemplateLoadingStrategy;
import com.dragome.templates.interfaces.TemplateManager;

public class GuiaServiceLocator
{
	protected static GuiaServiceLocator instance;
	protected TemplateManager templateManager;
	protected GuiaServiceFactory serviceFactory;

	public static GuiaServiceLocator getInstance()
	{
		if (instance == null)
			instance= new GuiaServiceLocator();

		return instance;
	}

	public GuiaServiceLocator()
	{
		ReflectionService reflectionService= ServiceLocator.getInstance().getReflectionService();
		Class<? extends GuiaServiceFactory> type= reflectionService.getSubTypesOf(GuiaServiceFactory.class).iterator().next();
		serviceFactory= reflectionService.createClassInstance(type);
	}
	
	public TemplateManager getTemplateManager()
	{
		if (templateManager == null)
		{
			//			if (isClientSide())
			templateManager= serviceFactory.createTemplateManager();
			//			else
			//				templateManager= getServerSideServiceFactory().createSyncService(TemplateManager.class);
		}

		return templateManager;
	}

	public TemplateHandler getTemplateHandler()
	{
		return serviceFactory.createTemplateHandler();
	}

	public TemplateLoadingStrategy getTemplateHandlingStrategy()
	{
		return serviceFactory.createTemplateHandlingStrategy();
	}

	public void setServiceFactory(GuiaServiceFactory serviceFactory)
	{
		this.serviceFactory= serviceFactory;
	}

	public TemplateListener getTemplateListener()
	{
		return serviceFactory.getTemplateListener();
	}

	public StyleChangedListener getStyleChangeListener()
	{
		return serviceFactory.getStyleChangeListener();
	}
}
