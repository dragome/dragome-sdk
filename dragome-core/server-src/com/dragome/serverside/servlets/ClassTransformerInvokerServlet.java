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
package com.dragome.serverside.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dragome.serverside.debugging.websocket.ClassTransformerDragomeWebSocketHandler;
import com.dragome.services.AbstractServicesInvocationHandler;

//@WebServlet(value= "/" + AbstractServicesInvocationHandler.SERVICE_INVOKER_PATH)
public class ClassTransformerInvokerServlet extends GetPostServlet
{
	protected void doService(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		try
		{
			String parameter= req.getParameter(AbstractServicesInvocationHandler.INVOCATION);

			String result= (String) ClassTransformerDragomeWebSocketHandler.executeMethod("com.dragome.serverside.servlets.ServiceInvoker", "invoke", parameter);
			resp.setCharacterEncoding("utf-8");
			if (result != null)
				resp.getWriter().write(result);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void init() throws ServletException
	{
		super.init();
	}
}
