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

import java.io.IOException;

import com.dragome.model.interfaces.Renderer;

public final class SimpleRenderer<T> implements Renderer<T>
{
	public String render(T object)
	{
		return object != null ? object.toString() : "";
	}
	public void render(T object, Appendable appendable) throws IOException
	{
		appendable.append(render(object));
	}
}
