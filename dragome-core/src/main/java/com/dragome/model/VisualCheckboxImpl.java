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

import com.dragome.model.interfaces.VisualCheckbox;
import com.dragome.model.listeners.ClickListener;

public class VisualCheckboxImpl extends SelectableButtonComponent implements VisualCheckbox
{
	public VisualCheckboxImpl()
    {
		init();
    }
	
	public VisualCheckboxImpl(String aName, String aCaption, Boolean selected)
	{
		super(aName, aCaption);
		setValue(selected);
		init();
	}

	public VisualCheckboxImpl(String aName)
	{
		super(aName);
		init();
	}

	public VisualCheckboxImpl(String aName, String aCaption, ClickListener clickListener)
	{
		super(aName, aCaption);
		init();
		addClickListener(clickListener);
	}

	public VisualCheckboxImpl(String aName, ClickListener clickListener)
	{
		this(aName, "", clickListener);
	}

}
