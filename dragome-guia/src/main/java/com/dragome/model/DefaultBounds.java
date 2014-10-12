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

import com.dragome.model.interfaces.VisualBounds;

public class DefaultBounds implements VisualBounds
{
    public static final Object ZERO= new DefaultBounds(0, 0, 0, 0);
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private BoundsChangeListener boundsChangeListener= new BoundsChangeListener()
    {
	public void boundsChanged()
	{
	}
    };

    public DefaultBounds()
    {
    }

    public DefaultBounds(int aX, int aY, int aWidth, int aHeight)
    {
	x= aX;
	y= aY;
	width= aWidth;
	height= aHeight;
    }

    public DefaultBounds(int aX, int aY, int aWidth, int aHeight, BoundsChangeListener boundsChangeListener)
    {
	this(aX, aY, aWidth, aHeight);
	this.boundsChangeListener= boundsChangeListener;
    }

    public boolean equals(Object obj)
    {
	if (obj instanceof VisualBounds)
	{
	    VisualBounds otherBounds= (VisualBounds) obj;

	    return getWidth() == otherBounds.getWidth() && getHeight() == otherBounds.getHeight() && getX() == otherBounds.getX() && getY() == otherBounds.getY();
	}

	return false;
    }

    public int getHeight()
    {
	return height;
    }

    public int getWidth()
    {
	return width;
    }

    public int getX()
    {
	return x;
    }

    public int getY()
    {
	return y;
    }

    public void setHeight(int aHeight)
    {
	height= aHeight;
	boundsChangeListener.boundsChanged();
    }

    public void setWidth(int aWidth)
    {
	width= aWidth;
	boundsChangeListener.boundsChanged();
    }

    public void setX(int aX)
    {
	x= aX;
	boundsChangeListener.boundsChanged();
    }

    public void setY(int aY)
    {
	y= aY;
	boundsChangeListener.boundsChanged();
    }
}
