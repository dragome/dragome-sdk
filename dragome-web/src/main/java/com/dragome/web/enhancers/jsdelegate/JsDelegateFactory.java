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

import com.dragome.commons.javascript.ScriptHelper;

public class JsDelegateFactory
{
	public static <T> T createFromNode(Object instance, Class<T> type)
	{
		try
		{
			String delegateClassName= JsDelegateGenerator.createDelegateClassName(type.getName());
			Class<?> class2= Class.forName(delegateClassName);
			Object newInstance= class2.newInstance();
			ScriptHelper.put("delegate", newInstance, null);
			ScriptHelper.put("instance", instance, null);
			ScriptHelper.eval("delegate.node= instance.node", null);
			
			return (T) newInstance;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static <T> T createFrom(Object instance, Class<T> type)
	{
		try
		{
			String delegateClassName= JsDelegateGenerator.createDelegateClassName(type.getName());
			Class<?> class2= Class.forName(delegateClassName);
			Object newInstance= class2.newInstance();
			ScriptHelper.put("delegate", newInstance, null);
			ScriptHelper.put("instance", instance, null);
			ScriptHelper.eval("delegate.node= instance", null);
			
			return (T) newInstance;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
}
