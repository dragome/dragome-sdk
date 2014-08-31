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

public class TemplateBindingBuilder extends BaseBuilder<VisualComponent, TemplateBindingBuilder>
{
	private Template template;

	public Template getTemplate()
	{
		return template;
	}

	public VisualPanel getPanel()
	{
		return panel;
	}

	private VisualPanel panel;

	public TemplateBindingBuilder(VisualPanel panel, Template child, BaseBuilder<? extends VisualComponent, ?> parentBuilder)
	{
		this.panel= panel;
		this.template= child;
		this.parentBuilder= (BaseBuilder<? extends VisualComponent, TemplateBindingBuilder>) parentBuilder;
	}

	public <C extends VisualComponent> TemplateComponentBindingBuilder<C> as(Class<C> componentType)
	{
		return new TemplateComponentBindingBuilder<C>(template, panel, componentType, parentBuilder);
	}

	public <C extends VisualComponent> TemplateComponentBindingBuilder<C> to(C component)
	{
		return new TemplateComponentBindingBuilder<C>(template, panel, component, parentBuilder);
	}

	public VisualComponent build()
	{
		return panel;
	}
}
