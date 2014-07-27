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


import com.dragome.forms.bindings.client.form.binding.FormBinder;
import com.dragome.forms.bindings.client.form.metadata.binding.ConditionBinderBuilder;
import com.dragome.methodlogger.enhancers.MethodInvocationListener;
import com.dragome.methodlogger.enhancers.MethodInvocationLogger;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Template;

public class ComponentBuilder<T>
{
	private FormBinder binder= new FormBinder();
	protected ConditionBinderBuilder<?> conditionBinderBuilder;

	private Template template;
	private VisualPanel panel;
	private T model;

	public ComponentBuilder(VisualPanel aPanel, T model)
	{
		this(aPanel);
		this.model= model;

		configureMethodListener();
	}

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
		this.panel= component;
		if (component instanceof VisualPanel && ((VisualPanel) component).getLayout() instanceof TemplateLayout)
		{
			TemplateLayout templateLayout= (TemplateLayout) ((VisualPanel) component).getLayout();
			template= templateLayout.getTemplate();
		}
	}

	public TemplateBindingBuilder<T> bindTemplate(String aChildTemplateName)
	{
		return new TemplateBindingBuilder<T>(panel, template.getChild(aChildTemplateName), model);
	}

	public DragomeStyleBuilder<T> style(VisualComponent component)
	{
		DragomeStyleBuilder<T> builder= new DragomeStyleBuilder<T>(model);
		builder.style(component);

		return builder;
	}

	public DragomeStyleBuilder<T> styleWith(String className)
	{
		return style(panel).with(className);
	}

	public VisualPanel panel()
	{
		return panel;
	}

	public T getModel()
	{
		return model;
	}

	public void showWhen(Supplier<Boolean> supplier)
	{
		show(panel).when(supplier);
	}

	public ComponentBuilder<T> show(VisualComponent visualComponent)
	{
		conditionBinderBuilder= binder.show(visualComponent);
		return this;
	}

	public void when(Supplier<Boolean> object)
	{
		ValueModelDelegator<Object, Boolean> condition= BindingSync.createCondition(object, model);
		conditionBinderBuilder.when(condition);
	}
}
