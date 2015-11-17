package com.dragome.render.serverside.swing;

import com.dragome.guia.GuiaServiceFactory;
import com.dragome.guia.events.listeners.interfaces.StyleChangedListener;
import com.dragome.model.interfaces.Style;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.templates.interfaces.TemplateListener;
import com.dragome.templates.interfaces.TemplateLoadingStrategy;
import com.dragome.templates.interfaces.TemplateManager;

public class SwingGuiaServiceFactory implements GuiaServiceFactory
{
	public TemplateManager createTemplateManager()
	{
		return new SwingTemplateManager();
	}

	public TemplateHandler createTemplateHandler()
	{
		return new SwingTemplateHandler();
	}

	public TemplateLoadingStrategy createTemplateHandlingStrategy()
	{
		return new SwingTemplateLoadingStrategy();
	}

	public TemplateListener getTemplateListener()
	{
		return new SwingTemplateListener();
	}

	public StyleChangedListener getStyleChangeListener()
	{
		return new StyleChangedListener()
		{
			public void styleChanged(Style style)
			{
			}

			public void boundsChanged(Style style)
			{
			}
		};
	}
}
