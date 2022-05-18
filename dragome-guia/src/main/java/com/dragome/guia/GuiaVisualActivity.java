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

import com.dragome.guia.components.VisualPanelImpl;
import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.services.interfaces.ParametersHandler;
import com.dragome.services.interfaces.ServiceFactory;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateLoadingStrategy;
import com.dragome.view.DefaultVisualActivity;
import com.dragome.view.VisualActivity;

public abstract class GuiaVisualActivity extends DefaultVisualActivity implements VisualActivity
{
	protected TemplateLoadingStrategy templateHandlingStrategy= GuiaServiceLocator.getInstance().getTemplateHandlingStrategy();
	protected VisualPanel mainPanel;
	protected Template mainTemplate;
	
	public GuiaVisualActivity()
	{
	}

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
	public TemplateLoadingStrategy getTemplateHandlingStrategy()
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
