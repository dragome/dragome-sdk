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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Element;

import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.canvas.interfaces.ConcatCanvas;

public class HTMLConcatCanvas implements ConcatCanvas<Element>
{
	protected boolean horizontal;
	protected List<Element> content= new ArrayList<Element>();

	public HTMLConcatCanvas()
	{
	}

	public HTMLConcatCanvas(boolean horizontal)
	{
		this.horizontal= horizontal;
	}

	public void concat(Canvas<Element> canvas)
	{
		if (canvas instanceof ConcatCanvas)
			content.addAll((Collection<? extends Element>) canvas.getContent());
		else
			content.add(canvas.getContent());
	}

	public void setContent(List<Element> object)
	{
		content= object;
	}

	public List<Element> getContent()
	{
		return content;
	}

	public void replaceSection(String anAlias, Canvas<?> aCanvas)
	{
	}
}
