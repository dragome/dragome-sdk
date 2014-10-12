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

import com.dragome.model.interfaces.SelectionModeChooser;

public class DefaultSelectionModeChooser implements SelectionModeChooser
{
	protected SelectionMode selectionMode= SelectionMode.SINGLE_SELECTION;

	public DefaultSelectionModeChooser()
	{
	}

	public SelectionMode getSelectionMode()
	{
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode aSelectionMode)
	{
		selectionMode= aSelectionMode;
	}
}
