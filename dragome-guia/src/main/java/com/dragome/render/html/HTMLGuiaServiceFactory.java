package com.dragome.render.html;

import com.dragome.guia.GuiaServiceFactory;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.templates.HTMLTemplateManager;
import com.dragome.templates.interfaces.TemplateLoadingStrategy;
import com.dragome.templates.interfaces.TemplateListener;
import com.dragome.templates.interfaces.TemplateManager;

public class HTMLGuiaServiceFactory implements GuiaServiceFactory
{
	public TemplateManager createTemplateManager()
	{
		return new HTMLTemplateManager();
	}

	public TemplateHandler createTemplateHandler()
	{
		return new HTMLTemplateHandler();
	}

	public TemplateLoadingStrategy createTemplateHandlingStrategy()
	{
		return new HTMLTemplateLoadingStrategy();
	}

	public TemplateListener getTemplateListener()
	{
		return new HTMLTemplateChangedListener();
	}
}
