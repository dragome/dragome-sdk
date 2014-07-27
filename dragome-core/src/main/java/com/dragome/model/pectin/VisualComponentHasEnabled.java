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
package com.dragome.model.pectin;

import com.dragome.model.interfaces.HasEnabled;
import com.dragome.model.interfaces.VisualComponent;

public class VisualComponentHasEnabled implements HasEnabled
{
	private VisualComponent visualComponent;

	public VisualComponentHasEnabled(VisualComponent widget)
	{
		this.visualComponent= widget;
	}
	public void setEnabled(boolean enabled)
	{
		visualComponent.getStyle().setEnabled(enabled);
	}
	public boolean isEnabled()
	{
		return visualComponent.getStyle().isEnabled();
	}
}
