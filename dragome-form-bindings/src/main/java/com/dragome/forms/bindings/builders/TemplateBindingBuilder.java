/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.forms.bindings.builders;

import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.templates.interfaces.Template;

public class TemplateBindingBuilder<T>
{
	private Template template;
	private VisualPanel panel;
	private T model;

	public TemplateBindingBuilder(VisualPanel panel, Template child, T model)
	{
		this.panel= panel;
		this.template= child;
		this.model= model;
	}

	public <C extends VisualComponent> TemplateComponentBindingBuilder<T, C> as(Class<C> componentType)
	{
		return new TemplateComponentBindingBuilder<T, C>(template, panel, model, componentType);
	}
	
	public <C extends VisualComponent> TemplateComponentBindingBuilder<T, C> to(C component)
	{
		return new TemplateComponentBindingBuilder<T, C>(template, panel, model, component);
	}

}
