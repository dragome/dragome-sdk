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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dragome.commons.javascript.ScriptHelper;

public class BrowserElement extends AbstractElement
{
	private String id;
	private boolean initialized= true;

	public BrowserElement()
	{
	}

	public BrowserElement(boolean initialized)
	{
		this.initialized= initialized;
	}

	public BrowserElement(String tagName)
	{
		ScriptHelper.put("tagName", tagName, this);
		ScriptHelper.evalNoResult("this.node= document.createElement(tagName);", this);
	}

	public Node appendChild(Node newChild) throws DOMException
	{
		init();

		ScriptHelper.put("aChild", newChild, this);
		ScriptHelper.eval("this.node.appendChild(aChild.node)", this);
		return newChild;
	}
	public Node cloneNode(boolean deep)
	{
		init();

		BrowserElement clonedNode= new BrowserElement();
		ScriptHelper.put("clonedNode", clonedNode, this);
		ScriptHelper.put("deep", deep, this);
		ScriptHelper.evalNoResult("clonedNode.node= this.node.cloneNode(deep);", this);
		String identityHashCode= System.identityHashCode(clonedNode) + "";
		clonedNode.setIdOnly(identityHashCode);
		return clonedNode;
	}

	public String getAttribute(String name)
	{
		init();

		return getAttributeNode(name).getValue();
	}

	public Attr getAttributeNode(String name)
	{
		init();

		return new BrowserAttribute(this, name);
	}

	public NodeList getChildNodes()
	{
		init();

		List<Element> result= new ArrayList<Element>();
		int childCount= ScriptHelper.evalInt("this.node.childNodes.length", this);

		for (int i= 0; i < childCount; i++)
		{
			ScriptHelper.put("i", i, this);
			if (ScriptHelper.evalBoolean("this.node.childNodes[i].tagName", this))
			{
				BrowserElement element= new BrowserElement();
				ScriptHelper.put("element", element, this);
				ScriptHelper.evalNoResult("element.node= this.node.childNodes[i];", this);
				ScriptHelper.put("elementId", System.identityHashCode(element), this);
				//		ScriptHelper.evalNoResult("if (!element.node.id) element.node.id= elementId;", this);
				result.add(element);
			}
		}

		return new BrowserNodeList(result);
	}

	public NodeList getElementsByTagName(String tagName)
	{
		init();

		List<Element> result= new ArrayList<Element>();

		ScriptHelper.put("tagName", tagName, this);
		ScriptHelper.put("tempNodes", "", this);
		ScriptHelper.evalNoResult("tempNodes=this.node.getElementsByTagName(tagName)", this);

		int childCount= ScriptHelper.evalInt("tempNodes.length", this);

		for (int i= 0; i < childCount; i++)
		{
			ScriptHelper.put("i", i, this);
			if (ScriptHelper.evalBoolean("tempNodes[i].tagName", this))
			{
				BrowserElement element= new BrowserElement();
				ScriptHelper.put("element", element, this);
				ScriptHelper.evalNoResult("element.node= tempNodes[i];", this);
				ScriptHelper.put("elementId", System.identityHashCode(element), this);
				//ScriptHelper.evalNoResult("element.node.id2= elementId;", this); //TODO revisar id!!
				result.add(element);
			}
		}

		return new BrowserNodeList(result);
	}

	public String getId()
	{
		return id;
	}

	public String getNodeName()
	{
		init();

		return (String) ScriptHelper.eval("this.node.tagName", this);
	}

	public Node getParentNode()
	{
		init();

		BrowserElement parentElement= null;
		if (ScriptHelper.evalBoolean("this.node.parentNode != null", this))
		{
			parentElement= new BrowserElement();
			ScriptHelper.put("parentElement", parentElement, this);
			ScriptHelper.evalNoResult("parentElement.node= this.node.parentNode;", this);
		}
		return parentElement;
	}

	private void init()
	{
		if (!initialized)
		{
			if (id != null)
			{
				setId(id);
				initialized= true;
			}
		}
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException
	{
		init();

		ScriptHelper.put("newChild", newChild, this);
		ScriptHelper.put("refChild", refChild, this);
		ScriptHelper.evalNoResult("this.node.insertBefore(newChild.node, refChild.node)", this);
		return newChild;
	}

	public void removeAttribute(String aName)
	{
		init();

		ScriptHelper.put("aName", aName, this);
		ScriptHelper.evalNoResult("this.node.removeAttribute(aName);", this);
	}

	public Node removeChild(Node aChild)
	{
		init();

		ScriptHelper.put("aChild", aChild, this);
		ScriptHelper.evalNoResult("this.node.removeChild(aChild.node)", this);
		return aChild;
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException
	{
		init();

		ScriptHelper.put("aNewChild", newChild, this);
		ScriptHelper.put("anOldChild", oldChild, this);
		ScriptHelper.evalNoResult("this.node.replaceChild(aNewChild.node, anOldChild.node)", this);
		return newChild;
	}

	public void setAttribute(String aName, String aValue)
	{
		init();

		BrowserAttribute lazyAttribute= new BrowserAttribute(this, aName);
		lazyAttribute.setValue(aValue);
	}

	public boolean setId(String id)
	{
		this.id= id;
		ScriptHelper.put("elementId", id, this);
		ScriptHelper.evalNoResult("this.node= getElementByDebugId(elementId) || document.getElementById(elementId)", this);
		boolean exists= ScriptHelper.evalBoolean("this.node != undefined && this.node != null", this);
		//	if (exists)
		//	    ScriptHelper.evalNoResult("this.node.id= elementId;", this);

		initialized= true;

		return exists;
	}

	public void setIdOnly(String id)
	{
		this.id= id;
	}

	public String toString()
	{
		init();

		//	NamedNodeMap attributes= getAttributes();

		//	StringBuilder result= new StringBuilder();
		//	result.append("<");
		//	result.append(getNodeName());
		//	for (int i= 0; i < attributes.getLength(); i++)
		//	{
		//	    Node item= attributes.item(i);
		//	    result.append(" "+item.getNodeName() + "= " + item.getNodeValue());
		//	}
		//	result.append(">");
		return id;
		//return (String) ScriptHelper.eval("getOuterHTML(this.node)", this);
	}

	public NamedNodeMap getAttributes()
	{
		return new BrowserNamedNodeMap(this);
	}

	public Node getNextSibling()
	{
		init();

		BrowserElement parentElement= null;
		if (ScriptHelper.evalBoolean("this.node.nextSibling != null", this))
		{
			parentElement= new BrowserElement();
			ScriptHelper.put("nextSibling", parentElement, this);
			ScriptHelper.evalNoResult("nextSibling.node= this.node.nextSibling", this);
		}
		return parentElement;
	}
}
