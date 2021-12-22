package com.dragome.web.enhancers.jsdelegate;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class BaseDelegateElement implements Element
{
	private Element element;

	public BaseDelegateElement()
	{
	}

	public BaseDelegateElement(Element element)
	{
		this.setElement(element);
	}

	public String getTagName()
	{
		return getElement().getTagName();
	}

	public String getAttribute(String name)
	{
		return getElement().getAttribute(name);
	}

	public void setAttribute(String name, String value) throws DOMException
	{
		getElement().setAttribute(name, value);
	}

	public void removeAttribute(String name) throws DOMException
	{
		getElement().removeAttribute(name);
	}

	public Attr getAttributeNode(String name)
	{
		return getElement().getAttributeNode(name);
	}

	public Attr setAttributeNode(Attr newAttr) throws DOMException
	{
		return getElement().setAttributeNode(newAttr);
	}

	public Attr removeAttributeNode(Attr oldAttr) throws DOMException
	{
		return getElement().removeAttributeNode(oldAttr);
	}

	public String getNodeName()
	{
		return getElement().getNodeName();
	}

	public String getNodeValue() throws DOMException
	{
		return getElement().getNodeValue();
	}

	public NodeList getElementsByTagName(String name)
	{
		return getElement().getElementsByTagName(name);
	}

	public void setNodeValue(String nodeValue) throws DOMException
	{
		getElement().setNodeValue(nodeValue);
	}

	public String getAttributeNS(String namespaceURI, String localName) throws DOMException
	{
		return getElement().getAttributeNS(namespaceURI, localName);
	}

	public short getNodeType()
	{
		return getElement().getNodeType();
	}

	public Node getParentNode()
	{
		return getElement().getParentNode();
	}

	public NodeList getChildNodes()
	{
		return getElement().getChildNodes();
	}

	public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException
	{
		getElement().setAttributeNS(namespaceURI, qualifiedName, value);
	}

	public Node getFirstChild()
	{
		return getElement().getFirstChild();
	}

	public Node getLastChild()
	{
		return getElement().getLastChild();
	}

	public Node getPreviousSibling()
	{
		return getElement().getPreviousSibling();
	}

	public Node getNextSibling()
	{
		return getElement().getNextSibling();
	}

	public NamedNodeMap getAttributes()
	{
		return getElement().getAttributes();
	}

	public Document getOwnerDocument()
	{
		return getElement().getOwnerDocument();
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException
	{
		return getElement().insertBefore(newChild, refChild);
	}

	public void removeAttributeNS(String namespaceURI, String localName) throws DOMException
	{
		getElement().removeAttributeNS(namespaceURI, localName);
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException
	{
		return getElement().replaceChild(newChild, oldChild);
	}

	public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException
	{
		return getElement().getAttributeNodeNS(namespaceURI, localName);
	}

	public Node removeChild(Node oldChild) throws DOMException
	{
		return getElement().removeChild(oldChild);
	}

	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException
	{
		return getElement().setAttributeNodeNS(newAttr);
	}

	public Node appendChild(Node newChild) throws DOMException
	{
		return getElement().appendChild(newChild);
	}

	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException
	{
		return getElement().getElementsByTagNameNS(namespaceURI, localName);
	}

	public boolean hasChildNodes()
	{
		return getElement().hasChildNodes();
	}

	public Node cloneNode(boolean deep)
	{
		return getElement().cloneNode(deep);
	}

	public boolean hasAttribute(String name)
	{
		return getElement().hasAttribute(name);
	}

	public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException
	{
		return getElement().hasAttributeNS(namespaceURI, localName);
	}

	public void normalize()
	{
		getElement().normalize();
	}

	public TypeInfo getSchemaTypeInfo()
	{
		return getElement().getSchemaTypeInfo();
	}

	public void setIdAttribute(String name, boolean isId) throws DOMException
	{
		getElement().setIdAttribute(name, isId);
	}

	public boolean isSupported(String feature, String version)
	{
		return getElement().isSupported(feature, version);
	}

	public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException
	{
		getElement().setIdAttributeNS(namespaceURI, localName, isId);
	}

	public String getNamespaceURI()
	{
		return getElement().getNamespaceURI();
	}

	public String getPrefix()
	{
		return getElement().getPrefix();
	}

	public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException
	{
		getElement().setIdAttributeNode(idAttr, isId);
	}

	public void setPrefix(String prefix) throws DOMException
	{
		getElement().setPrefix(prefix);
	}

	public String getLocalName()
	{
		return getElement().getLocalName();
	}

	public boolean hasAttributes()
	{
		return getElement().hasAttributes();
	}

	public String getBaseURI()
	{
		return getElement().getBaseURI();
	}

	public short compareDocumentPosition(Node other) throws DOMException
	{
		return getElement().compareDocumentPosition(other);
	}

	public String getTextContent() throws DOMException
	{
		return getElement().getTextContent();
	}

	public void setTextContent(String textContent) throws DOMException
	{
		getElement().setTextContent(textContent);
	}

	public boolean isSameNode(Node other)
	{
		return getElement().isSameNode(other);
	}

	public String lookupPrefix(String namespaceURI)
	{
		return getElement().lookupPrefix(namespaceURI);
	}

	public boolean isDefaultNamespace(String namespaceURI)
	{
		return getElement().isDefaultNamespace(namespaceURI);
	}

	public String lookupNamespaceURI(String prefix)
	{
		return getElement().lookupNamespaceURI(prefix);
	}

	public boolean isEqualNode(Node arg)
	{
		return getElement().isEqualNode(arg);
	}

	public Object getFeature(String feature, String version)
	{
		return getElement().getFeature(feature, version);
	}

	public Object setUserData(String key, Object data, UserDataHandler handler)
	{
		return getElement().setUserData(key, data, handler);
	}

	public Object getUserData(String key)
	{
		return getElement().getUserData(key);
	}

	public Element getElement()
	{
		return element;
	}

	public void setElement(Element element)
	{
		this.element = element;
	}
}
