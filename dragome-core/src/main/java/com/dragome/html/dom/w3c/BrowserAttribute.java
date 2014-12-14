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

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.dragome.commons.javascript.ScriptHelper;

public class BrowserAttribute extends AbstractAttr implements Attr
{
	private Element element;
	private String name;

	public BrowserAttribute()
	{
	}

	public BrowserAttribute(Element lazyElement, String aName)
	{
		this.element= lazyElement;
		this.name= aName;
	}
	public void setValue(String aValue)
	{
		ScriptHelper.put("foundElement", element, this);
		ScriptHelper.put("aValue", aValue, this);
		ScriptHelper.put("aName", name, this);

		if (this.element != null)
		{
			try
			{
				if (name.equals("class"))
					ScriptHelper.evalNoResult("foundElement.node.className= aValue;", this);
				else if (name.equals("innerHTML"))
					ScriptHelper.evalNoResult("foundElement.node.innerHTML= aValue;", this);
				else
					ScriptHelper.evalNoResult("foundElement.node.setAttribute(aName, aValue);", this);
			}
			catch (Exception e)
			{
			}
		}
	}

	public String getValue()
	{
		ScriptHelper.put("foundElement", element, this);

		String result;
		if ("outerHTML".equals(name))
			result= (String) ScriptHelper.eval("getOuterHTML(foundElement.node)", this);
		else if (name.equals("innerHTML"))
			result= (String) ScriptHelper.eval("foundElement.node.innerHTML", this);
		else
		{
			ScriptHelper.put("attributeName", name, this);
			result= (String) ScriptHelper.eval("foundElement.node.getAttribute(attributeName)", this);
			if (result == null)
				result= "";
		}

		//			Global.window.alert(aName + " _ getValue:" + result);
		return result;
	}
	public String getName()
	{
		return name;
	}
}
