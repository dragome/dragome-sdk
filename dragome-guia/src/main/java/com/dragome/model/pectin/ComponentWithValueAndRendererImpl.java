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
