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

import com.dragome.model.interfaces.Selectable;
import com.dragome.model.listeners.SelectionListener;

public class DefaultSelectable extends DefaultEventProducer implements Selectable
{
	protected boolean selected= false;

	public DefaultSelectable()
	{
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean isSelected)
	{
		selected= isSelected;
		if (hasListener(SelectionListener.class))
			getListener(SelectionListener.class).selectionChanged(DefaultSelectable.this);
	}

	public void select()
	{
		setSelected(true);
	}

	public void deselect()
	{
		setSelected(false);
	}
}
