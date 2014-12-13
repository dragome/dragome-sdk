/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
