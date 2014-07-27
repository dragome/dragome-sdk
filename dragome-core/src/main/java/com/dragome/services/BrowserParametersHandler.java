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
package com.dragome.services;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.interfaces.ParametersHandler;

public class BrowserParametersHandler implements ParametersHandler
{
	public String getParameter(String aKey)
	{
		ScriptHelper.put("aKey", aKey, this);
		return (String) ScriptHelper.eval("getQuerystring(aKey)", this);
	}

	public String getRequestURL()
	{
		return (String) ScriptHelper.eval("window.location.href", this);
	}

	public String getParameter(String aKey, String deaultValue)
	{
		String parameter= getParameter(aKey);
		if (parameter == null || parameter.trim().length() == 0)
			parameter= deaultValue;

		return parameter;
	}

	public String getFragment()
	{
		String requestURL= getRequestURL();
		if (requestURL.contains("#"))
			return requestURL.substring(requestURL.indexOf("#") + 1);
		else
			return null;
	}

	public void setFragment(String fragment)
	{
		ScriptHelper.put("fragment", fragment, this);
		ScriptHelper.evalNoResult("window.location.hash = fragment", this);
	}
}
