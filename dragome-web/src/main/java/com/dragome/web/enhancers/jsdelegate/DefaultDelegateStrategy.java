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

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class DefaultDelegateStrategy implements DelegateStrategy
{
	@Override
	public String createMethodCall(CtMethod method, StringBuffer code, String params) throws NotFoundException  {
		
		if(method.getName().startsWith("set") && method.getParameterTypes().length == 1)
		{ //
			CtClass parameterType= method.getParameterTypes()[0];
			code.append("$eval$(\"this.node." + method.getName().toLowerCase().charAt(3) + method.getName().substring(4) + "= " + JsDelegateGenerator.createVariableForEval("$1", parameterType) + "\", this);");
			return null;
		}
		else if(method.getName().startsWith("get") && method.getParameterTypes().length == 0)
		{
			code.append("$eval$(\"this.node." + method.getName().toLowerCase().charAt(3) + method.getName().substring(4) + "\", this);");
			return null;
		}
		
		return null;
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