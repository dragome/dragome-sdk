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
package com.dragome.guia.components;

import com.dragome.guia.components.interfaces.VisualColor;

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
