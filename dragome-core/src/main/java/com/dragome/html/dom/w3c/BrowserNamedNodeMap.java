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

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.dragome.commons.javascript.ScriptHelper;

public class BrowserNamedNodeMap implements NamedNodeMap
{
	private BrowserElement browserElement;

	public BrowserNamedNodeMap(BrowserElement browserElement)
	{
		this.browserElement= browserElement;
	}

	public Node getNamedItem(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node setNamedItem(Node arg) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node removeNamedItem(String name) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node item(int index)
	{
		ScriptHelper.put("element", browserElement, this);
		ScriptHelper.put("index", index, this);
		String name= (String) ScriptHelper.eval("element.node.attributes[index].name", this);

		return new BrowserAttribute(browserElement, name);
	}

	@Override
	public int getLength()
	{
		ScriptHelper.put("element", browserElement, this);
		int length= ScriptHelper.evalInt("element.node.attributes.length", this);

		return length;
	}

	@Override
	public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node setNamedItemNS(Node arg) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
