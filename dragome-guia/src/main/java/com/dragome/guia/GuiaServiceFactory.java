package com.dragome.guia;

import com.dragome.guia.events.listeners.interfaces.StyleChangedListener;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.templates.interfaces.TemplateListener;
import com.dragome.templates.interfaces.TemplateLoadingStrategy;
import com.dragome.templates.interfaces.TemplateManager;

public interface GuiaServiceFactory
{
	TemplateManager createTemplateManager();
	TemplateHandler createTemplateHandler();
	TemplateLoadingStrategy createTemplateHandlingStrategy();
	TemplateListener getTemplateListener();
	StyleChangedListener getStyleChangeListener();
}
