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
import com.dragome.model.interfaces.VisualLink;
import com.dragome.model.listeners.ClickListener;
import com.dragome.model.pectin.ComponentWithValueAndRendererImpl;

public class VisualLinkImpl extends ComponentWithValueAndRendererImpl<String> implements VisualLink
{
	private String url;

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url= url;
	}
	
	public VisualLinkImpl()
    {
		this("");
    }

	public VisualLinkImpl(String aName)
	{
		this(aName, new SimpleRenderer<String>());
	}

	public VisualLinkImpl(String aName, String aValue)
	{
		this(aName);
		setValue(aValue);
	}

	public VisualLinkImpl(String aName, Renderer<String> renderer)
	{
		super(aName, renderer);
	}

	public VisualLinkImpl(String aName, String aValue, String url)
	{
		this(aName, aValue);
		this.url= url;
	}

	public VisualLinkImpl(String aName, ClickListener clickListener)
    {
		this(aName);
		addClickListener(clickListener);
    }
}
