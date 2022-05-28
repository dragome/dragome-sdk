package com.dragome.render.html.renderers;

import java.util.Objects;

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.templates.interfaces.Template;

public class CompatibleTemplateKey
{
	private Class<? extends VisualComponent> componentType;
	private Template template;

	public CompatibleTemplateKey(Class<? extends VisualComponent> componentType, Template template)
	{
		this.componentType= componentType;
		this.template= template;
	}

	public int hashCode()
	{
		return Objects.hash(componentType);
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompatibleTemplateKey other= (CompatibleTemplateKey) obj;
		return Objects.equals(componentType, other.componentType) && template == other.template;
	}

}
