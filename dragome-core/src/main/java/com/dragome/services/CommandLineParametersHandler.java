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

		return "";
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
