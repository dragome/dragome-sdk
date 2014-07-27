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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLDocument;

import com.dragome.commons.javascript.ScriptHelper;

public class BrowserHtmlDocument extends AbstractHtmlDocument implements HTMLDocument
{
	public BrowserHtmlDocument()
	{
	}

	public Element createElement(String anElementName)
	{
		Element element= new BrowserElement(anElementName);

		//	if (anElementName != null && anElementName.trim().length() > 0)
		//	    element.setName(anElementName);

		return element;
	}

	public Element getElementById(String anId)
	{
		BrowserElement lazyElement= new BrowserElement();
		boolean exists= lazyElement.setId(anId);
		return exists ? lazyElement : null;
	}

	public Text createTextNode(String data)
	{
		return new BrowserText(data);
	}

	public Node appendChild(Node newChild) throws DOMException
	{
		ScriptHelper.put("aChild", newChild, this);
		ScriptHelper.eval("document.appendChild(aChild.node)", this);
		return newChild;
	}

	public NodeList getElementsByTagName(String tagName)
	{
		List<Element> result= new ArrayList<Element>();

		ScriptHelper.put("tagName", tagName, this);
		ScriptHelper.put("tempNodes", "", this);
		ScriptHelper.evalNoResult("tempNodes=document.getElementsByTagName(tagName)", this);

		int childCount= ScriptHelper.evalInt("tempNodes.length", this);

		for (int i= 0; i < childCount; i++)
		{
			ScriptHelper.put("i", i, this);
			if (ScriptHelper.evalBoolean("tempNodes[i].tagName", this))
			{
				BrowserElement element= new BrowserElement();
				ScriptHelper.put("element", element, this);
				ScriptHelper.evalNoResult("element.node= tempNodes[i];", this);
				//ScriptHelper.put("elementId", System.identityHashCode(element), this);
				//ScriptHelper.evalNoResult("element.node.id2= elementId;", this); //TODO revisar id!!
				result.add(element);
			}
		}

		return new BrowserNodeList(result);
	}
}
