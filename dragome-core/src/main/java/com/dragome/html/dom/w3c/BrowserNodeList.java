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
