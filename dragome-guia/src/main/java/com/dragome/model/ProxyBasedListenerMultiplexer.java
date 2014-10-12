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
package com.dragome.model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EventListener;

import com.dragome.helpers.collections.CollectionHandler;
import com.dragome.helpers.collections.ItemInvoker;
import com.dragome.model.interfaces.ListenerMultiplexer;
import com.dragome.remote.IndetifiableProxy;

public class ProxyBasedListenerMultiplexer<T extends EventListener> implements InvocationHandler, ListenerMultiplexer<T>, IndetifiableProxy<EventListener>
{
	protected CollectionHandler<T> collectionHandler;
	protected Class<? extends EventListener> proxiedClass;

	public ProxyBasedListenerMultiplexer(Class<? extends EventListener> aClass)
	{
		proxiedClass= aClass;
		collectionHandler= new CollectionHandler<T>();
	}

	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
	{
		if (method.getName().equals("add"))
		{
			add((T) args[0]);
		}
		else if (method.getName().equals("remove"))
		{
			remove((T) args[0]);
		}
		else
		{
			collectionHandler.forAll(new ItemInvoker<T>()
			{
				public void invoke(T item)
				{
					try
					{
						method.invoke(item, args);
					}
					catch (Exception e)
					{
						throw new RuntimeException(e);
					}
				}
			});
		}

		return null;
	}

	public void add(T aListener)
	{
		collectionHandler.add(aListener);
	}

	public void remove(T aListener)
	{
		collectionHandler.remove(aListener);
	}

	public static <T extends EventListener> T createListenerMultiplexer(Class<T> aType)
	{
		ProxyBasedListenerMultiplexer<T> invocationHandler= new ProxyBasedListenerMultiplexer(aType);
		T listenerMultiplexer= (T) Proxy.newProxyInstance(ProxyBasedListenerMultiplexer.class.getClassLoader(), new Class<?>[] { ListenerMultiplexer.class, aType }, invocationHandler);
		return listenerMultiplexer;
	}

	public Class<? extends EventListener> getProxiedClazz()
	{
		return proxiedClass;
	}

	public void setProxiedClazz(Class<? extends EventListener> clazz)
	{
		this.proxiedClass= clazz;
	}

}
