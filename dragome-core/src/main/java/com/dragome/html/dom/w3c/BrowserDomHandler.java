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
package com.dragome.html.dom.w3c;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.html.dom.DomHandler;

public class BrowserDomHandler implements DomHandler
{
	protected Document document;

	public BrowserDomHandler()
	{
	}

	public Document getDocument()
	{
		if (document == null)
			document= new BrowserHtmlDocument();

		return document;
	}

	public void setDocument(Document document)
	{
		this.document= document;
	}

	public Element getElementBySelector(String selector)
	{
		BrowserElement foundElement= new BrowserElement();

		ScriptHelper.put("foundElement", foundElement, this);
		ScriptHelper.put("selector", selector, this);
		ScriptHelper.evalNoResult("foundElement.node= document.querySelectorAll(selector)[0]", this);
		boolean exists= ScriptHelper.evalBoolean("foundElement.node != undefined && foundElement.node != null", this);

		return exists ? foundElement : null;
	}
}
