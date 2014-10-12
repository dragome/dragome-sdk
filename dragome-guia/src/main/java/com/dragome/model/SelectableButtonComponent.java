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

import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.listeners.ClickListener;

public class SelectableButtonComponent extends SelectableComponent
{
	protected String caption;

	public SelectableButtonComponent(String name, String aCaption)
	{
		this(name);
		caption= aCaption;
	}

	public SelectableButtonComponent()
	{
	}

	public SelectableButtonComponent(String aName)
	{
		super(aName);
	}

	public String getCaption()
	{
		return caption;
	}

	public void setCaption(String caption)
	{
		this.caption= caption;
	}
	
	protected void init()
	{
		setValue(false, false);

		addListener(ClickListener.class, new ClickListener()
		{
			public void clickPerformed(VisualComponent aVisualComponent)
			{
				setValue(!getValue());
			}
		});
	}
}
