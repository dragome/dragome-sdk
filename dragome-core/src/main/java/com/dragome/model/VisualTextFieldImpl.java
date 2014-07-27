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
package com.dragome.model;

import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.VisualTextField;
import com.dragome.model.pectin.ComponentWithValueAndRendererImpl;

public class VisualTextFieldImpl<T> extends ComponentWithValueAndRendererImpl<T> implements VisualTextField<T>
{
	public VisualTextFieldImpl()
	{
		this("");
	}

	public VisualTextFieldImpl(String aName)
	{
		this(aName, new SimpleRenderer<T>());
	}

	public VisualTextFieldImpl(String aName, T aValue)
	{
		this(aName);
		setValue(aValue);
	}

	public VisualTextFieldImpl(String aName, Renderer<T> renderer)
	{
		super(aName, renderer);
	}
}
