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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

public class AbstractHtmlDocument implements HTMLDocument
{
	public AbstractHtmlDocument()
	{
	}

	public DocumentType getDoctype()
	{

		return null;
	}

	public DOMImplementation getImplementation()
	{

		return null;
	}

	public Element getDocumentElement()
	{

		return null;
	}

	public Element createElement(String tagName) throws DOMException
	{

		return null;
	}

	public DocumentFragment createDocumentFragment()
	{

		return null;
	}

	public Text createTextNode(String data)
	{

		return null;
	}

	public Comment createComment(String data)
	{

		return null;
	}

	public CDATASection createCDATASection(String data) throws DOMException
	{

		return null;
	}

	public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException
	{

		return null;
	}

	public Attr createAttribute(String name) throws DOMException
	{

		return null;
	}

	public EntityReference createEntityReference(String name) throws DOMException
	{

		return null;
	}

	public NodeList getElementsByTagName(String tagname)
	{

		return null;
	}

	public Node importNode(Node importedNode, boolean deep) throws DOMException
	{

		return null;
	}

	public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException
	{

		return null;
	}

	public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException
	{

		return null;
	}

	public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
	{

		return null;
	}

	public Element getElementById(String elementId)
	{

		return null;
	}

	public String getInputEncoding()
	{

		return null;
	}

	public String getXmlEncoding()
	{

		return null;
	}

	public boolean getXmlStandalone()
	{

		return false;
	}

	public void setXmlStandalone(boolean xmlStandalone) throws DOMException
	{

	}

	public String getXmlVersion()
	{

		return null;
	}

	public void setXmlVersion(String xmlVersion) throws DOMException
	{

	}

	public boolean getStrictErrorChecking()
	{

		return false;
	}

	public void setStrictErrorChecking(boolean strictErrorChecking)
	{

	}

	public String getDocumentURI()
	{

		return null;
	}

	public void setDocumentURI(String documentURI)
	{

	}

	public Node adoptNode(Node source) throws DOMException
	{

		return null;
	}

	public DOMConfiguration getDomConfig()
	{

		return null;
	}

	public void normalizeDocument()
	{

	}

	public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException
	{

		return null;
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

	public String getTitle()
	{

		return null;
	}

	public void setTitle(String title)
	{

	}

	public String getReferrer()
	{

		return null;
	}

	public String getDomain()
	{

		return null;
	}

	public String getURL()
	{

		return null;
	}

	public HTMLElement getBody()
	{

		return null;
	}

	public void setBody(HTMLElement body)
	{

	}

	public HTMLCollection getImages()
	{

		return null;
	}

	public HTMLCollection getApplets()
	{

		return null;
	}

	public HTMLCollection getLinks()
	{

		return null;
	}

	public HTMLCollection getForms()
	{

		return null;
	}

	public HTMLCollection getAnchors()
	{

		return null;
	}

	public String getCookie()
	{

		return null;
	}

	public void setCookie(String cookie)
	{

	}

	public void open()
	{

	}

	public void close()
	{

	}

	public void write(String text)
	{

	}

	public void writeln(String text)
	{

	}

	public NodeList getElementsByName(String elementName)
	{

		return null;
	}
}
