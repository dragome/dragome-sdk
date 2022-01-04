package com.dragome.web.html.dom.w3c;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;

public interface ElementExtension extends Element, EventTarget
{
	public Element querySelector(String selectors);
	public NodeList querySelectorAll(String selectors);
	public void setInnerHTML(final String newHtml);
	public String getInnerHTML();
}
