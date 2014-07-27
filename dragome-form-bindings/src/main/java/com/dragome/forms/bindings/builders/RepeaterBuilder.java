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

import java.util.ArrayList;
import java.util.List;

import com.dragome.model.VisualPanelImpl;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.templates.SimpleItemProcessor;
import com.dragome.templates.TemplateRepeater;
import com.dragome.templates.interfaces.Template;

public class RepeaterBuilder<T, S>
{
	private ValueModelDelegator<S, List<T>> valueModelDelegator;
	private Template mainTemplate;
	private Template template;
	private Getter<S, Tester<T>> filter;
	private S model;
	private VisualPanel panel;

	public RepeaterBuilder(ValueModelDelegator<S, List<T>> valueModelDelegator, Template template, S model, VisualPanel panel)
	{
		this.valueModelDelegator= valueModelDelegator;
		this.template= template;
		this.model= model;
		this.panel= panel;
	}

	public RepeaterBuilder<T, S> repeat(final ItemRepeater<T> itemRepeater)
	{
		final List<T> list= new ArrayList<T>(valueModelDelegator.getValue());

		final TemplateRepeater<T> templateRepeater= new TemplateRepeater<T>(list, template.getParent(), template.getName(), new SimpleItemProcessor<T>()
		{
			public void fillTemplate(T item, Template aTemplate)
			{
				VisualPanelImpl itemPanel= new VisualPanelImpl(aTemplate);
				panel.addChild(itemPanel);
				ComponentBuilder<T> componentBuilder= new ComponentBuilder<T>(itemPanel, item);
				itemRepeater.process(item, componentBuilder);
			}
		}, true);
		templateRepeater.repeatItems();

		valueModelDelegator.addValueChangeHandler(new ValueChangeHandler<List<T>>()
		{
			public void onValueChange(ValueChangeEvent<List<T>> event)
			{
				List<T> value= valueModelDelegator.getValue();
				List<T> collectedList= collectItems(value);

				list.clear();
				list.addAll(collectedList);

				templateRepeater.clearAndRepeatItems();
			}

			private List<T> collectItems(List<T> value)
			{
				List<T> collectedList= new ArrayList<T>();
				for (T t : value)
					if (filter == null || filter.get(model).test(t))
						collectedList.add(t);

				// Java 8 Stream Implementation
				// List<T> collectedList= valueModelDelegator.getValue().stream().filter(filter.get(model)).collect(Collectors.toList());
				return collectedList;
			}
		});

		return this;
	}

	public RepeaterBuilder<T, S> filter(Getter<S, Tester<T>> aFilter)
	{
		this.filter= aFilter;
		return this;
	}

}
