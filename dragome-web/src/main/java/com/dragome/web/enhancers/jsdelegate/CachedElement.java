package com.dragome.web.enhancers.jsdelegate;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CachedElement extends BaseDelegateElement
{
	private ElementData elementData;

	public CachedElement()
	{
	}
	
	public CachedElement(ElementData elementData, Element element)
	{
		super(element);
		this.setElementData(elementData);
	}

	public String getAttribute(String name)
	{
		return getElementData().getAttribute(name);
	}

	public void setAttribute(String name, String value) throws DOMException
	{
		getElementData().addAttribute(name, value);
		super.setAttribute(name, value);
	}

	public String getNodeName()
	{
		return getElementData().getNodeName();
	}

	public String getTagName()
	{
		return getElementData().getTagName();
	}

	public boolean isSameNode(Node other)
	{
		if (other instanceof CachedElement)
		{
			CachedElement cachedElement= (CachedElement) other;
			String elementId= getAttribute("data-element-id");
			return elementId.equals(cachedElement.getAttribute("data-element-id"));
		}
		else
			return super.isSameNode(other);
	}

	public ElementData getElementData()
	{
		if (elementData == null)
			elementData= JsCast.elementRepository.getElementData(getElement());

		return elementData;
	}

	public void setElementData(ElementData elementData)
	{
		this.elementData= elementData;
	}
}
