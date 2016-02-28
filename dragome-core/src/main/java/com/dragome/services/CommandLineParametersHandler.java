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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dragome.services.interfaces.ParametersHandler;

public class CommandLineParametersHandler implements ParametersHandler
{
	public String getParameter(String aKey)
	{
		
		if (aKey.equals("debug"))
			return "false";
		if (aKey.equals("class"))
			return "net.ar.unfeca.both.ListaPersonasDemo4";
		if (aKey.equals("refresh"))
			return "false";

		String property= System.getProperty("parameters").replace("[", "").replace("]", "");
		return getParameterValue(property, aKey);
	}

	public static String getParameterValue(String input, String parameter) {
	    return input.replaceAll("(.*("+parameter+"=([\\.\\w]+)).*)|.*", "$3");
	}

	
	@Override
	public String getRequestURL()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameter(String string, String deaultValue)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public String getFragment()
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void setFragment(String fragment)
    {
	    // TODO Auto-generated method stub
	    
    }
}
