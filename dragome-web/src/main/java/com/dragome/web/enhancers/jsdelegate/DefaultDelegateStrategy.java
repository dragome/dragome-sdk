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
import com.dragome.w3c.dom.events.EventListener;
import com.dragome.w3c.dom.events.EventTarget;
import com.dragome.w3c.dom.typedarray.ArrayBufferView;
import com.dragome.web.enhancers.jsdelegate.interfaces.DelegateStrategy;
import com.dragome.web.enhancers.jsdelegate.interfaces.SubTypeFactory;
import com.google.common.reflect.TypeToken;

public class DefaultDelegateStrategy implements DelegateStrategy
{
	@MethodAlias(alias= "window.getSimpleClassname")
	public String getSimpleClassname(Class<?> instance)
	{
		return instance.getSimpleName();
	}

	public String createMethodCall(Method method, String params)
	{
		if (params == null)
			params= "";

		String result= "";
		String name= method.getName();

		Class<?>[] superclass= method.getDeclaringClass().getInterfaces();
		boolean isTypedArray= superclass.length > 0 && superclass[0].equals(ArrayBufferView.class);

		if (isTypedArray && name.equals("set") && method.getParameterTypes().length == 2 && method.getParameterTypes()[0].equals(int.class))
			result= "this.node[$1] = $2";
		else if (isTypedArray && (name.equals("get") || name.equals("getAsDouble")) && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(int.class))
			result= "this.node[$1]";
		else if (name.startsWith("set") && name.length() > 3 && method.getParameterTypes().length == 1)
		{
			Class<?> parameterType= method.getParameterTypes()[0];
			result= "this.node." + name.toLowerCase().charAt(3) + name.substring(4) + "= " + JsDelegateGenerator.createVariableForEval("$1", parameterType);
		}
		else if (name.startsWith("get") && name.length() > 3 && method.getParameterTypes().length == 0)
		{
			result= "this.node." + name.toLowerCase().charAt(3) + name.substring(4);
		}
		else if (name.equals("createInstanceOf"))
		{
			result= "eval('new '+ getSimpleClassname($1) + '(" + params.replace("$1, ", "") + ")')";
		}
		else
		{
			result= "this.node." + name + "(" + params + ")";
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

	public String createReturnExpression(Class<?> clazz, Method method, String returnTypeAsString)
	{

		if (method.getName().equals("createInstanceOf"))
			return "return " + JsCast.class.getName() + ".castTo(temp, $1);";
		else
		{
			if (returnTypeAsString == null)
			{
				Class<?> rawType= TypeToken.of(clazz).resolveType(method.getGenericReturnType()).getRawType();
				returnTypeAsString= rawType.getName() + ".class";
			}
			return "return " + JsCast.class.getName() + ".castTo(temp, " + returnTypeAsString + ");";
		}
	}

	public String createMethodBody(Method method, String params)
	{
		Class<?> declaringClass= method.getDeclaringClass();

		if (EventTarget.class.isAssignableFrom(declaringClass))
		{
			String methodName= method.getName();
			if (method.getParameterTypes().length > 0 && EventListener.class.isAssignableFrom(method.getParameterTypes()[0]))
			{
				if (methodName.startsWith("set"))
					methodName= methodName.substring(3, 4).toLowerCase() + methodName.substring(4);

				return JsCast.class.getName() + ".addOnEventListener (this, $1, \"" + methodName + "\");";
			}
			else if (methodName.equals("addEventListener"))
			{
				String parametersDeclaration= "$1, $2";
				if (method.getParameterTypes().length == 3)
					parametersDeclaration+= ", $3";

				return JsCast.class.getName() + ".addEventListener (this, " + parametersDeclaration + ");";
			}
		}

		return null;
	}
}