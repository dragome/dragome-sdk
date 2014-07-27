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
package com.dragome.render.html;

import com.dragome.render.interfaces.HtmlTemplateStorage;

public class HtmlTemplateStorageImpl implements HtmlTemplateStorage
{
	public String getHtmlContent(String string)
	{
		return "<html><body><span id=\"body-content\">hola mundo!</span></body></html>";
	}
}
