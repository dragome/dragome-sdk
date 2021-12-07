package com.dragome.render.html;

import com.dragome.annotations.ServiceImplementation;
import com.dragome.templates.interfaces.Template;

@ServiceImplementation(TemplateServiceImpl.class)
public interface TemplateService
{
	Template cloneTemplate(Template template);
}
