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
package com.dragome.serverside.debugging.websocket;

import java.io.IOException;
import java.lang.reflect.Method;

import org.atmosphere.config.service.WebSocketHandlerService;
import org.atmosphere.util.SimpleBroadcaster;
import org.atmosphere.websocket.WebSocket;
import org.atmosphere.websocket.WebSocketHandlerAdapter;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.services.ServiceLocator;

@WebSocketHandlerService(path= "/dragome-websocket", broadcaster= SimpleBroadcaster.class)
public class ClassTransformerDragomeWebSocketHandler extends WebSocketHandlerAdapter
{
	private static ClassLoader classLoader;

	public void onClose(WebSocket webSocket)
	{
	};

	public void onOpen(WebSocket webSocket) throws IOException
	{
		executeMethod(getClass().getPackage().getName() + ".DragomeWebSocketHandler", "onOpen", webSocket);
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

			Class<?> loadClass= classLoader.loadClass(className);

			for (Method method : loadClass.getMethods())
			{
				if (method.getName().equals(methodName))
					return method.invoke(loadClass.newInstance(), args);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		return null;
	}

	public void onByteMessage(WebSocket webSocket, byte[] data, int offset, int length) throws IOException
	{
	}

	public void onTextMessage(WebSocket webSocket, String message) throws IOException
	{
		executeMethod(getClass().getPackage().getName() + ".DragomeWebSocketHandler", "onTextMessage", webSocket, message);
	}
}
