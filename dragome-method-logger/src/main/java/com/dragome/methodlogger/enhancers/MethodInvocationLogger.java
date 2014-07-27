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

package com.dragome.methodlogger.enhancers;

public class MethodInvocationLogger
{
	private static MethodInvocationListener listener;

	public static void onMethodEnter(Object instance, String name)
	{
		if (listener != null)
			listener.onMethodEnter(instance, name);
	}

	public static void onMethodExit(Object instance, String name)
	{
		if (listener != null)
			listener.onMethodExit(instance, name);
	}

	public static void setListener(MethodInvocationListener listener)
	{
		MethodInvocationLogger.listener= listener;
	}

	public static MethodInvocationListener getListener()
	{
		return listener;
	}
}
