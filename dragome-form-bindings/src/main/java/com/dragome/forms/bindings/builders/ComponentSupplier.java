package com.dragome.forms.bindings.builders;

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.templates.interfaces.Template;

public interface ComponentSupplier
{
	public VisualComponent get(Template template);
}
