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

import com.dragome.model.interfaces.VisualColor;

public class DefaultColor implements VisualColor
{
	protected int red;
	protected int green;
	protected int blue;

	public DefaultColor()
	{
	}

	public DefaultColor(int red2, int green2, int blue2)
	{
		red= red2;
		green= green2;
		blue= blue2;
	}

	public int getRed()
	{
		return red;
	}

	public int getGreen()
	{
		return green;
	}

	public int getBlue()
	{
		return blue;
	}

	public boolean equals(Object obj)
	{
		if (obj instanceof DefaultColor)
		{
			DefaultColor otherColor= (DefaultColor) obj;
			return otherColor.getRed() == getRed() && otherColor.getBlue() == getBlue() && otherColor.getGreen() == getGreen();
		}
		else
			return false;
	}

	public void setBlue(int aBlue)
	{
		blue= aBlue;
	}

	public void setGreen(int aGreen)
	{
		green= aGreen;
	}

	public void setRed(int aRed)
	{
		red= aRed;
	}
}
