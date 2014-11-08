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

import com.dragome.methodlogger.enhancers.MethodInvocationListener;
import com.dragome.methodlogger.enhancers.MethodInvocationLogger;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualPanel;
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

				public void onMethodEnter(Object instance, String name)
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

				public void onMethodExit(Object instance, String name)
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
		return new TemplateBindingBuilder((VisualPanel) component, template.getChild(aChildTemplateName), parentBuilder);
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
