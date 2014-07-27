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
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

import com.dragome.commons.javascript.ScriptHelper;

public class BrowserText implements Text
{
	public BrowserText(String data)
	{
		setData(data);
	}

	public String getData() throws DOMException
	{
		return (String) ScriptHelper.eval("this.node", this);
	}

	public void setData(String data) throws DOMException
	{
		ScriptHelper.put("data", data, this);
		ScriptHelper.evalNoResult("this.node= document.createTextNode(data);", this);
	}

	public int getLength()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public String substringData(int offset, int count) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void appendData(String arg) throws DOMException
	{
		// TODO Auto-generated method stub

	}

	public void insertData(int offset, String arg) throws DOMException
	{
		// TODO Auto-generated method stub

	}

	public void deleteData(int offset, int count) throws DOMException
	{
		// TODO Auto-generated method stub

	}

	public void replaceData(int offset, int count, String arg) throws DOMException
	{
		// TODO Auto-generated method stub

	}

	public String getNodeName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getNodeValue() throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setNodeValue(String nodeValue) throws DOMException
	{
		// TODO Auto-generated method stub

	}

	public short getNodeType()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public Node getParentNode()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public NodeList getChildNodes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Node getFirstChild()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Node getLastChild()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Node getPreviousSibling()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Node getNextSibling()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public NamedNodeMap getAttributes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Document getOwnerDocument()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Node removeChild(Node oldChild) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Node appendChild(Node newChild) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildNodes()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Node cloneNode(boolean deep)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void normalize()
	{
		// TODO Auto-generated method stub

	}

	public boolean isSupported(String feature, String version)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public String getNamespaceURI()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getPrefix()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setPrefix(String prefix) throws DOMException
	{
		// TODO Auto-generated method stub

	}

	public String getLocalName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasAttributes()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Text splitText(int offset) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getBaseURI()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public short compareDocumentPosition(Node other) throws DOMException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public String getTextContent() throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setTextContent(String textContent) throws DOMException
	{
		// TODO Auto-generated method stub

	}

	public boolean isSameNode(Node other)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public String lookupPrefix(String namespaceURI)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDefaultNamespace(String namespaceURI)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public String lookupNamespaceURI(String prefix)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEqualNode(Node arg)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Object getFeature(String feature, String version)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object setUserData(String key, Object data, UserDataHandler handler)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object getUserData(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isElementContentWhitespace()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public String getWholeText()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Text replaceWholeText(String content) throws DOMException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
