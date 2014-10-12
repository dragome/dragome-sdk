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
package com.dragome.model.interfaces;

public interface VisualBounds
{
	public int getHeight();
	public int getWidth();
	public int getX();
	public int getY();
	public void setHeight(int aHeight);
	public void setWidth(int aWidth);
	public void setX(int aX);
	public void setY(int aY);
}
