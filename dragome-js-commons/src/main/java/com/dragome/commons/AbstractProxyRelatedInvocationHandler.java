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
package com.dragome.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractProxyRelatedInvocationHandler implements ProxyRelatedInvocationHandler
{
	private Object proxy;
	private Class<?>[] interfaces;

	public AbstractProxyRelatedInvocationHandler()
	{
	}

	public void setProxy(Object proxy)
	{
		this.proxy= proxy;
	}

	public Object getProxy()
	{
		return proxy;
	}

	public List<Class<?>> getInterfaces()
	{
		return interfaces == null ? new ArrayList<Class<?>>() : Arrays.asList(interfaces);
	}

	public void setInterfaces(List<Class<?>> interfaces)
	{
		this.interfaces= interfaces.toArray(new Class[0]);
	}
}
