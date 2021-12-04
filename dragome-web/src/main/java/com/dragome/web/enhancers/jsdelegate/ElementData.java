package com.dragome.web.enhancers.jsdelegate;

import java.util.HashMap;
import java.util.Map;

public class ElementData
{
	private Map<String, String> attributes= new HashMap<>();
	private String tagName;
	private String nodeName;

	public ElementData()
	{
	}

	public String getAttribute(String attributeName)
	{
		if ("class".equals(attributeName))
			attributeName= "clazz";
		
		return attributes.get(attributeName);
	}

	public void addAttribute(String attributeName, String value)
	{
		if ("class".equals(attributeName))
			attributeName= "clazz";
		
		attributes.put(attributeName, value);
	}

	public String getNodeName()
	{
		return nodeName;
	}

	public String getTagName()
	{
		return tagName;
	}

	public void setTagName(String tagName)
	{
		this.tagName= tagName;

	}

	public void setNodeName(String nodeName)
	{
		this.nodeName= nodeName;
	}

	public Map<String, String> getAttributes()
	{
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes)
	{
		this.attributes= attributes;
	}

}
