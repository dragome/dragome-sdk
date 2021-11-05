package com.dragome.templates;

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.templates.interfaces.Template;

public interface TemplateChangeListener
{

	void changingTemplate(VisualComponent associatedComponent, Template currentTemplate, Template newTemplate);

}
