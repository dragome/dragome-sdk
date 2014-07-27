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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BrowserNodeList implements NodeList
{
	protected List<Element> nodes= new ArrayList<Element>();

	public BrowserNodeList()
	{
	}

	public BrowserNodeList(List<Element> nodes)
	{
		this.nodes= nodes;
	}
	public Node item(int index)
	{
		return nodes.get(index);
	}
	public int getLength()
	{
		return nodes.size();
	}
}
