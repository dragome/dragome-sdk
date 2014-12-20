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
