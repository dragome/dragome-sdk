package com.dragome.web.html.dom.w3c;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public interface ElementExtension extends Element
{
	public Element querySelector(String selectors);
	public NodeList querySelectorAll(String selectors);
}
