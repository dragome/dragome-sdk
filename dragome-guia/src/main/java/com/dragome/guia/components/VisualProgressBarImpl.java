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
package com.dragome.guia.components;

import com.dragome.guia.components.interfaces.VisualProgressBar;
import com.dragome.model.interfaces.Renderer;
import com.dragome.model.pectin.ComponentWithValueAndRendererImpl;

public class VisualProgressBarImpl extends ComponentWithValueAndRendererImpl<Integer> implements VisualProgressBar
{
	protected int maximum= 100;
	protected Orientation orientation;
	protected String text;

	public VisualProgressBarImpl()
	{
	}

	public VisualProgressBarImpl(String aName)
	{
		this(aName, new SimpleRenderer<Integer>());
	}

	public VisualProgressBarImpl(String aName, Integer aValue)
	{
		this(aName);
		setValue(aValue);
	}

	public VisualProgressBarImpl(String aName, Renderer<Integer> renderer)
	{
		super(aName, renderer);
	}

	public int getMaximum()
	{
		return maximum;
	}

	public Orientation getOrientation()
	{
		return orientation;
	}

	public String getText()
	{
		return text;
	}

	public void setMaximum(int maximum)
	{
		this.maximum= maximum;
	}
	public void setOrientation(Orientation orientation)
	{
		this.orientation= orientation;
	}
	public void setText(String text)
	{
		this.text= text;
	}

	public int getPercentage()
	{
		int result= getValue() * 100 / getMaximum();
		return result < 100 ? result : 100;
	}
}
