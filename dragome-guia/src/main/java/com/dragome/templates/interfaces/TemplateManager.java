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
package com.dragome.templates.interfaces;

import com.dragome.render.canvas.interfaces.CanvasFactory;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.render.interfaces.TemplateHandler;

public interface TemplateManager
{
//	public Template getTemplate(String anAlias);
	public TemplateHandler getTemplateHandler();
	public TemplateHandlingStrategy getTemplateHandlingStrategy();
	public CanvasFactory getCanvasFactory();
	public Template createTemplate(String aTemplateName);
	public ComponentRenderer getComponentRenderer();
}
