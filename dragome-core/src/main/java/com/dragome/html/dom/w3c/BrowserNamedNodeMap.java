/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
