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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class AbstractElement implements Element
{
	public AbstractElement()
	{
	}

	public String getNodeName()
	{

		return null;
	}

	public String getNodeValue() throws DOMException
	{

		return null;
	}

	public void setNodeValue(String nodeValue) throws DOMException
	{

	}

	public short getNodeType()
	{

		return 0;
	}

	public Node getParentNode()
	{

		return null;
	}

	public NodeList getChildNodes()
	{

		return null;
	}

	public Node getFirstChild()
	{

		return null;
	}

	public Node getLastChild()
	{

		return null;
	}

	public Node getPreviousSibling()
	{

		return null;
	}

	public Node getNextSibling()
	{

		return null;
	}

	public NamedNodeMap getAttributes()
	{

		return null;
	}

	public Document getOwnerDocument()
	{

		return null;
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException
	{

		return null;
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException
	{

		return null;
	}

	public Node removeChild(Node oldChild) throws DOMException
	{

		return null;
	}

	public Node appendChild(Node newChild) throws DOMException
	{

		return null;
	}

	public boolean hasChildNodes()
	{

		return false;
	}

	public Node cloneNode(boolean deep)
	{

		return null;
	}

	public void normalize()
	{

	}

	public boolean isSupported(String feature, String version)
	{

		return false;
	}

	public String getNamespaceURI()
	{

		return null;
	}

	public String getPrefix()
	{

		return null;
	}

	public void setPrefix(String prefix) throws DOMException
	{

	}

	public String getLocalName()
	{

		return null;
	}

	public boolean hasAttributes()
	{

		return false;
	}

	public String getBaseURI()
	{

		return null;
	}

	public short compareDocumentPosition(Node other) throws DOMException
	{

		return 0;
	}

	public String getTextContent() throws DOMException
	{

		return null;
	}

	public void setTextContent(String textContent) throws DOMException
	{

	}

	public boolean isSameNode(Node other)
	{

		return false;
	}

	public String lookupPrefix(String namespaceURI)
	{

		return null;
	}

	public boolean isDefaultNamespace(String namespaceURI)
	{

		return false;
	}

	public String lookupNamespaceURI(String prefix)
	{

		return null;
	}

	public boolean isEqualNode(Node arg)
	{

		return false;
	}

	public Object getFeature(String feature, String version)
	{

		return null;
	}

	public Object setUserData(String key, Object data, UserDataHandler handler)
	{

		return null;
	}

	public Object getUserData(String key)
	{

		return null;
	}

	public String getTagName()
	{

		return null;
	}

	public String getAttribute(String name)
	{

		return null;
	}

	public void setAttribute(String name, String value) throws DOMException
	{

	}

	public void removeAttribute(String name) throws DOMException
	{

	}

	public Attr getAttributeNode(String name)
	{

		return null;
	}

	public Attr setAttributeNode(Attr newAttr) throws DOMException
	{

		return null;
	}

	public Attr removeAttributeNode(Attr oldAttr) throws DOMException
	{

		return null;
	}

	public NodeList getElementsByTagName(String name)
	{

		return null;
	}

	public String getAttributeNS(String namespaceURI, String localName) throws DOMException
	{

		return null;
	}

	public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException
	{

	}

	public void removeAttributeNS(String namespaceURI, String localName) throws DOMException
	{

	}

	public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException
	{

		return null;
	}

	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException
	{

		return null;
	}

	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException
	{

		return null;
	}

	public boolean hasAttribute(String name)
	{

		return false;
	}

	public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException
	{

		return false;
	}

	public TypeInfo getSchemaTypeInfo()
	{

		return null;
	}

	public void setIdAttribute(String name, boolean isId) throws DOMException
	{

	}

	public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException
	{

	}

	public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException
	{

	}
}
