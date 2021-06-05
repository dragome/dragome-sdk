/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.forms.bindings.builders;

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.templates.interfaces.Template;

public class TemplateBindingBuilder extends BaseBuilder<VisualComponent, TemplateBindingBuilder>
{
	private Template template;

	public Template getTemplate()
	{
		return template;
	}

	public VisualComponent getPanel()
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
