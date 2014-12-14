/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
