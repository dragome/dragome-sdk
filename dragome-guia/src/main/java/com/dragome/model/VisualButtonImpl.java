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

import com.dragome.model.interfaces.VisualButton;
import com.dragome.model.listeners.ClickListener;

public class VisualButtonImpl extends SelectableButtonComponent implements VisualButton
{
	public VisualButtonImpl()
	{
	}

	public VisualButtonImpl(String aName)
	{
		super(aName, "");
	}

	public VisualButtonImpl(String aName, String aCaption)
	{
		super(aName, aCaption);
	}

	public VisualButtonImpl(String aName, String aCaption, ClickListener clickListener)
	{
		super(aName, aCaption);
		addClickListener(clickListener);
	}

	public VisualButtonImpl(String aName, ClickListener clickListener)
	{
		this(aName, "", clickListener);
	}
}
