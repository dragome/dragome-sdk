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
import com.dragome.model.interfaces.VisualImage;
import com.dragome.model.pectin.ComponentWithValueAndRendererImpl;

public class VisualImageImpl extends ComponentWithValueAndRendererImpl<String> implements VisualImage
{
        public VisualImageImpl()
        {
        }
        
	public VisualImageImpl(String aName)
	{
		this(aName, new SimpleRenderer<String>());
	}

	public VisualImageImpl(String aName, String aValue)
	{
		this(aName);
		setValue(aValue);
	}

	public VisualImageImpl(String aName, Renderer<String> renderer)
	{
		super(aName, renderer);
	}
}
