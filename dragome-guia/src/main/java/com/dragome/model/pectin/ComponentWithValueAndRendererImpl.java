/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.model.pectin;

import com.dragome.model.AbstractVisualComponent;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.HasRenderer;
import com.dragome.model.interfaces.HasValue;
import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

public class ComponentWithValueAndRendererImpl<T> extends AbstractVisualComponent implements HasValue<T>, HasRenderer<T>
{
	private T value;
	private Renderer<T> renderer;

	public ComponentWithValueAndRendererImpl()
	{
	}

	public ComponentWithValueAndRendererImpl(String name)
	{
		super(name);
	}

	public ComponentWithValueAndRendererImpl(String aName, Renderer<T> renderer)
	{
		super(aName);
		this.renderer= renderer;
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(T value)
	{
		setValue(value, true);
	}

	public void setValue(T value, boolean fireEvents)
	{
		boolean changed= this.value != null ? !this.value.equals(value) : value != null;
		this.value= value;
		if (fireEvents && changed)
			fireValueChange(value);
	}

	public void fireValueChange(T value)
	{
		if (hasListener(ValueChangeHandler.class))
			getListener(ValueChangeHandler.class).onValueChange(new ValueChangeEvent<T>(value));
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler)
	{
		addListener(ValueChangeHandler.class, handler);
		return new DummyHandlerRegistration<T>();
	}

	public Renderer<T> getRenderer()
	{
		return renderer;
	}

	public void setRenderer(Renderer<T> renderer)
	{
		this.renderer= renderer;
	}
}
