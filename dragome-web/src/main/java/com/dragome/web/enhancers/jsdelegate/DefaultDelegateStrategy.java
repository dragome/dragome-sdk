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

import java.lang.reflect.Method;

import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.web.enhancers.jsdelegate.interfaces.DelegateStrategy;
import com.dragome.web.enhancers.jsdelegate.interfaces.SubTypeFactory;

public class DefaultDelegateStrategy implements DelegateStrategy
{
	@MethodAlias(alias= "window.getSimpleClassname")
	public String getSimpleClassname(Class<?> instance)
	{
		return instance.getSimpleName();
	}

	public String createMethodCall(Method method, String params)
	{
		String result= "";
		if (method.getName().startsWith("set") && method.getParameterTypes().length == 1)
		{
			Class<?> parameterType= method.getParameterTypes()[0];
			result= "this.node." + method.getName().toLowerCase().charAt(3) + method.getName().substring(4) + "= " + JsDelegateGenerator.createVariableForEval("$1", parameterType);
		}
		else if (method.getName().startsWith("get") && method.getParameterTypes().length == 0)
		{
			result= "this.node." + method.getName().toLowerCase().charAt(3) + method.getName().substring(4);
		}
		else if (method.getName().equals("createInstanceOf"))
		{
			result= "eval('new '+ getSimpleClassname($1) + '(" + params.replace("$1, ", "") + ")')";
		}
		else
		{
			result= "this.node." + method.getName() + "(" + params + ")";
		}

		return result;
	}

	public String getSubTypeExtractorFor(Class<?> interface1, String methodName)
	{
		return null;
	}

	public Class<? extends SubTypeFactory> getSubTypeFactoryClassFor(Class<?> interface1, String methodName)
	{
		return null;
	}

	public String createReturnExpression(Method method)
	{
		if (method.getName().equals("createInstanceOf"))
			return "return " + JsDelegateFactory.class.getName() + ".createFrom(temp, $1);";
		else
			return "return " + JsDelegateFactory.class.getName() + ".createFrom(temp, " + method.getReturnType().getName() + ".class" + ");";
	}
}