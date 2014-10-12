/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.services;

import com.dragome.render.html.HTMLTemplateHandler;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.templates.HTMLTemplateManager;
import com.dragome.templates.interfaces.TemplateManager;

public class GuiaServiceLocator
{
	protected static GuiaServiceLocator instance;
	protected TemplateManager templateManager;

	public static GuiaServiceLocator getInstance()
	{
		if (instance == null)
			instance= new GuiaServiceLocator();

		return instance;
	}

	public TemplateManager getTemplateManager()
	{
		if (templateManager == null)
		{
			//			if (isClientSide())
			templateManager= new HTMLTemplateManager();
			//			else
			//				templateManager= getServerSideServiceFactory().createSyncService(TemplateManager.class);
		}

		return templateManager;
	}

	public TemplateHandler getTemplateHandler()
	{
		return new HTMLTemplateHandler();
	}
}
