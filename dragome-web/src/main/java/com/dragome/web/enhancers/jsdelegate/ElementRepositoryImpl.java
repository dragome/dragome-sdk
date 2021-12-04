package com.dragome.web.enhancers.jsdelegate;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.html.dom.w3c.ElementExtension;

public class ElementRepositoryImpl implements ElementRepository
{
	public ElementData getElementData(Object anElement)
	{
		//		Element element= WebServiceLocator.getInstance().getDomHandler().getElementBySelector("[data-element-id=\"" + elementId + "\"]");

		ScriptHelper.put("e1", anElement, this);
		Element element= ScriptHelper.evalCasting("e1.node", Element.class, this);

		ElementData elementData= new ElementData();

		final NamedNodeMap attributes= element.getAttributes();
		final int attrCount= attributes.getLength();
		for (int i= 0; i < attrCount; i++)
		{
			Node attribute= attributes.item(i);
			String value= attribute.getNodeValue();
			if (value != null)
			{
				String nodeName= attribute.getNodeName();
				nodeName= nodeName.replace("class", "clazz");
				elementData.addAttribute(nodeName, value);
			}
		}

		elementData.setTagName(element.getTagName());
		elementData.setNodeName(element.getNodeName());

		return elementData;
	}

	private static int i;

	public Element cloneElement(Element node)
	{
		String clonedNumber= node.getAttribute("data-cloned-element");

		if (clonedNumber == null)
			node.setAttribute("data-cloned-element", "" + i++);

		Element cloneNode= (Element) node.cloneNode(true);

		ElementExtension elementExtension= JsCast.castTo(cloneNode, ElementExtension.class);

		NodeList querySelectorAll= elementExtension.querySelectorAll("[data-element-id]");
		for (int i= 0; i < querySelectorAll.getLength(); i++)
		{
			Element element= JsCast.castTo(querySelectorAll.item(i), Element.class);
			element.removeAttribute("data-element-id");
		}
		return cloneNode;
	}

}
