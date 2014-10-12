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
package com.dragome.render.canvas;

import org.w3c.dom.Element;

import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.canvas.interfaces.CanvasFactory;
import com.dragome.render.canvas.interfaces.CanvasHelper;
import com.dragome.render.canvas.interfaces.CanvasRenderer;
import com.dragome.render.canvas.interfaces.ConcatCanvas;

public class HTMLCanvasFactory implements CanvasFactory<Element>
{
	public Canvas<Element> createCanvas()
	{
		return new HTMLCanvas();
	}

	public ConcatCanvas<Element> createConcatCanvas(boolean horizontal)
	{
		return new HTMLConcatCanvas(horizontal);
	}

	public CanvasRenderer<Element> createCanvasRenderer()
	{
		return new HTMLCanvasRenderer();
	}

	public CanvasHelper getCanvasHelper()
	{
		return new HTMLCanvasHelper();
	}
}
