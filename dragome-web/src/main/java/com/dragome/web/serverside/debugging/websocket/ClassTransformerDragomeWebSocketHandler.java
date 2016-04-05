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

import java.io.IOException;
import java.lang.reflect.Method;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.atmosphere.config.service.WebSocketHandlerService;
import org.atmosphere.util.SimpleBroadcaster;
import org.atmosphere.websocket.WebSocket;
import org.atmosphere.websocket.WebSocketHandlerAdapter;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.services.ServiceLocator;

@WebSocketHandlerService(path= "/dragome-websocket", broadcaster= SimpleBroadcaster.class)
@ServerEndpoint(value= "/dragome-debug")
public class ClassTransformerDragomeWebSocketHandler extends WebSocketHandlerAdapter
{
	private static ClassLoader classLoader;

	public void onOpen(WebSocket webSocket) throws IOException
	{
		executeMethod(getClassName(), "onOpen", webSocket);
	}

	public void onTextMessage(WebSocket webSocket, String message) throws IOException
	{
		executeMethod(getClassName(), "onTextMessage", webSocket, message);
	}

	public void onClose(WebSocket webSocket)
	{
		executeMethod(getClassName(), "onClose", webSocket);
	}

	private String getClassName()
	{
		return getClass().getPackage().getName() + ".DragomeWebSocketHandler";
	}

	private String getClassName2()
	{
		return getClass().getPackage().getName() + ".DragomeDebugServerEndpoint";
	}

	public void onByteMessage(WebSocket webSocket, byte[] data, int offset, int length) throws IOException
	{
	}

	@OnOpen
	public void onOpen(final Session session)
	{
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

			Class<?> loadClass= classLoader.loadClass(className);

			for (Method method : loadClass.getMethods())
			{
				if (method.getName().equals(methodName))
					return method.invoke(loadClass.newInstance(), args);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return null;
	}
}
