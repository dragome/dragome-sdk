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
package com.dragome.html.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface DomHandler
{
	public Document getDocument();
	public void setDocument(Document document);
	public Element getElementBySelector(String selector);
	public void initDocument();
}
