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
import com.dragome.methodlogger.enhancers.MethodInvocationListener;
import com.dragome.methodlogger.enhancers.MethodInvocationLogger;
import com.dragome.templates.TemplateImpl;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Template;

public class ComponentBuilder extends BaseBuilder<VisualComponent, ComponentBuilder>
{
	private Template template;

	private void configureMethodListener()
	{
		if (MethodInvocationLogger.getListener() == null)
			MethodInvocationLogger.setListener(new MethodInvocationListener()
			{
				private int setters= 0;

				public synchronized void onMethodEnter(Object instance, String name)
				{
					if (isGetter(name))
					{
						if (!BindingSync.recordingFor.isEmpty())
							BindingSync.recordingFor.peek().addMethodVisitedEvent(new MethodVisitedEvent(instance, name));
					}
					else if (isSetter(name))
					{
						setters++;
						BindingSync.addEvent(new MethodVisitedEvent(instance, name));
					}
				}

				public synchronized void onMethodExit(Object instance, String name)
				{
					if (isSetter(name))
					{
						setters--;
						if (setters == 0)
							BindingSync.fireChanges();
					}
				}

				private boolean isGetter(String name)
				{
					return name.startsWith("get") || name.startsWith("is");
				}

				private boolean isSetter(String name)
				{
					return name.startsWith("set");
				}
			});
	}

	public ComponentBuilder(VisualPanel component)
	{
		this.component= component;
		if (component instanceof VisualPanel && ((VisualPanel) component).getLayout() instanceof TemplateLayout)
		{
			TemplateLayout templateLayout= (TemplateLayout) ((VisualPanel) component).getLayout();
			template= templateLayout.getTemplate();
		}

		if (template == null)
			throw new RuntimeException("VisualPanel has not template associated");

		configureMethodListener();
	}

	public ComponentBuilder(VisualPanel itemPanel, BaseBuilder<? extends VisualComponent, ?> templateComponentBindingBuilder)
	{
		this(itemPanel);
		this.parentBuilder= (BaseBuilder<? extends VisualComponent, ComponentBuilder>) templateComponentBindingBuilder;

	}

	public TemplateBindingBuilder bindTemplate(String aChildTemplateName)
	{
		Template child;
		if (template.hasChild(aChildTemplateName))
			child= template.getChild(aChildTemplateName);
		else
			child= TemplateImpl.findTemplate(getTopParent(template), aChildTemplateName);

		return new TemplateBindingBuilder((VisualPanel) component, child, parentBuilder);
	}

	private Template getTopParent(Template template)
	{
		while (template.getParent() != null)
			template= template.getParent();

		return template;
	}

	public TemplateBindingBuilder template(String aChildTemplateName)
	{
		return bindTemplate(aChildTemplateName);
	}

	public VisualPanel panel()
	{
		return (VisualPanel) component;
	}

	public VisualComponent build()
	{
		return component; //throw new RuntimeException("component is not ready");
	}
}
