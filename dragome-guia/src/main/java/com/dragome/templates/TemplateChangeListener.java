package com.dragome.templates;

import java.util.EventListener;

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.templates.interfaces.Template;

public interface TemplateChangeListener extends EventListener
{
	void changingTemplate(VisualComponent associatedComponent, Template currentTemplate, Template newTemplate);
}
