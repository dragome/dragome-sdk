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
package com.dragome.render.canvas.interfaces;

import org.w3c.dom.Element;

import com.dragome.templates.interfaces.Template;

public interface CanvasHelper
{
	void hideTemplateIds(Template template);
	void restoreTemplateIds(Template template);
	void removeReplacedElements(Element element);
}
