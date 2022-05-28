package com.dragome.render.serverside.swing;

import java.util.Optional;

import com.dragome.guia.components.interfaces.VisualButton;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.templates.interfaces.Template;

public abstract class AbstractSwingRenderer<T> implements ComponentRenderer<Object, T>
{

	public AbstractSwingRenderer()
	{
		super();
	}

	public boolean matches(Template child)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean matches(Template child, VisualComponent aVisualComponent)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTemplateCompatible(Template template)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Optional<Template> findMatchingTemplateFor(Class<? extends VisualComponent> componentType, Template template)
	{
		// TODO Auto-generated method stub
		return null;
	}

}