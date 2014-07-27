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
import com.dragome.model.interfaces.VisualTextArea;
import com.dragome.model.pectin.ComponentWithValueAndRendererImpl;

public class VisualTextAreaImpl<T> extends ComponentWithValueAndRendererImpl<T> implements VisualTextArea<T>
{
	public VisualTextAreaImpl()
	{
	}

	public VisualTextAreaImpl(String aName)
	{
		this(aName, new SimpleRenderer<T>());
	}

	public VisualTextAreaImpl(String aName, T aValue)
	{
		this(aName);
		setValue(aValue);
	}

	public VisualTextAreaImpl(String aName, Renderer<T> renderer)
	{
		super(aName, renderer);
	}
}
