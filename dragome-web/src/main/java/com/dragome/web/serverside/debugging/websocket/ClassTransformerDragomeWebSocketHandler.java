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
package com.dragome.web.serverside.debugging.websocket;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.services.ServiceLocator;

@ServerEndpoint(value= "/dragome-debug")
public class ClassTransformerDragomeWebSocketHandler
{
	private static ClassLoader classLoader;
	private static Map<String, Method> methods= Collections.synchronizedMap(new HashMap<>());

	private String getClassName2()
	{
		return getClass().getPackage().getName() + ".DragomeDebugServerEndpoint";
	}

	@OnOpen
	public void onOpen(final Session session)
	{
		session.setMaxIdleTimeout(0);
		session.setMaxTextMessageBufferSize(10000000);
		session.setMaxBinaryMessageBufferSize(10000000);
		executeMethod(getClassName2(), "onOpen", session);
	}

	@OnMessage
	public String onMessage(String message, Session session)
	{
		return (String) executeMethod(getClassName2(), "onMessage", message, session);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason)
	{
		executeMethod(getClassName2(), "onClose", session, closeReason);
	}

	public static Object executeMethod(String className, String methodName, Object... args)
	{
		try
		{
			if (classLoader == null)
			{
				ClassLoader lastContextClassLoader= Thread.currentThread().getContextClassLoader();
				classLoader= lastContextClassLoader;

				DragomeConfigurator configurator= ServiceLocator.getInstance().getConfigurator();
				if (configurator != null)
				{
					ClassLoader parentClassloader= ClassTransformerDragomeWebSocketHandler.class.getClassLoader();
					classLoader= configurator.getNewClassloaderInstance(parentClassloader, parentClassloader);
				}
			}

			Thread.currentThread().setContextClassLoader(classLoader);
			String key= className + "." + methodName;
			Method foundMethod= methods.get(key);

			Class<?> loadClass= classLoader.loadClass(className);

			if (foundMethod == null)
			{
				for (Method method : loadClass.getMethods())
					if (method.getName().equals(methodName))
					{
						methods.put(key, method);
						foundMethod= method;
					}
			}

			return foundMethod != null ? foundMethod.invoke(loadClass.newInstance(), args) : null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
