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

public class SelectionMode
{
	public static SelectionMode MULTIPLE_INTERVAL_SELECTION= new SelectionMode(0);
	public static SelectionMode SINGLE_SELECTION= new SelectionMode(1);
	private int mode;

	public SelectionMode()
	{
	}

	public SelectionMode(int aMode)
	{
		this.mode= aMode;
	}

	public boolean equals(Object obj)
	{
		return ((SelectionMode) obj).getMode() == getMode();
	}

	public int getMode()
	{
		return mode;
	}
	public void setMode(int mode)
	{
		this.mode= mode;
	}
}
