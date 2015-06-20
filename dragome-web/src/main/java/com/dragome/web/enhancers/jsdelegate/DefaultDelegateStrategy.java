/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package com.dragome.web.enhancers.jsdelegate;

import com.dragome.web.enhancers.jsdelegate.interfaces.DelegateStrategy;
import com.dragome.web.enhancers.jsdelegate.interfaces.SubTypeFactory;

import javassist.CtMethod;
import javassist.NotFoundException;

public class DefaultDelegateStrategy implements DelegateStrategy
{
	public boolean isPropertyWriteMethod(CtMethod method)
	{
		try
		{
			return method.getName().startsWith("set") && method.getParameterTypes().length == 1;
		}
		catch (NotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public boolean isPropertyReadMethod(CtMethod method)
	{
		try
		{
			return method.getName().startsWith("get") && method.getParameterTypes().length == 0;
		}
		catch (NotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public String getSubTypeExtractorFor(Class<?> interface1, String methodName)
	{
		return null;
	}

	public Class<? extends SubTypeFactory> getSubTypeFactoryClassFor(Class<?> interface1, String methodName)
	{
		return null;
	}
}