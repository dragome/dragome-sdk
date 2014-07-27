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
package com.dragome.model;

import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.VisualProgressBar;
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
