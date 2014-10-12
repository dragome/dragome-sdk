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
package com.dragome.debugging.execution;

import com.dragome.model.DefaultVisualActivity;
import com.dragome.model.VisualPanelImpl;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.remote.ServiceFactory;
import com.dragome.render.html.HTMLTemplateHandlingStrategy;
import com.dragome.services.interfaces.ParametersHandler;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateHandlingStrategy;

public abstract class DragomeVisualActivity extends DefaultVisualActivity implements VisualActivity
{
	protected TemplateHandlingStrategy templateHandlingStrategy= new HTMLTemplateHandlingStrategy();
	protected VisualPanel mainPanel;
	protected Template mainTemplate;

	public void onCreate()
	{
		initialize();
		updateMainPanel();
		build();
		showTemplate();
	}

	public VisualPanel getMainPanel()
	{
		return mainPanel;
	}

	public Template getMainTemplate()
	{
		return mainTemplate;
	}

	public ParametersHandler getParametersHandler()
	{
		return parametersHandler;
	}
	public ServiceFactory getServiceFactory()
	{
		return serviceFactory;
	}
	public TemplateHandlingStrategy getTemplateHandlingStrategy()
	{
		return templateHandlingStrategy;
	}

	public void initialize()
	{
	}

	protected void loadMainTemplate(String templateFilename)
	{
		templateHandlingStrategy.loadMainTemplate(templateFilename);
		//	templateHandlingStrategy.hideContainer();
		templateHandlingStrategy.setupContainer();
		updateMainPanel();
	}

	protected void showTemplate()
	{
		templateHandlingStrategy.showContainer();
	}

	protected void hideTemplate()
	{
		templateHandlingStrategy.hideContainer();
	}

	protected void updateMainPanel()
	{
		mainTemplate= templateHandlingStrategy.getMainTemplate();
		mainPanel= new VisualPanelImpl(mainTemplate);
	}
}
