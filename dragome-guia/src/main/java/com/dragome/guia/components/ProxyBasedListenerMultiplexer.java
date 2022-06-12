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
package com.dragome.guia.components;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EventListener;
import java.util.List;

import com.dragome.guia.events.listeners.interfaces.ListenerMultiplexer;
import com.dragome.guia.helper.collections.CollectionHandler;
import com.dragome.guia.helper.collections.ItemInvoker;
import com.dragome.model.IndetifiableProxy;

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
		else if (method.getName().equals("createUnmutableCopy"))
		{
			return createUnmutableCopy();
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

	public ListenerMultiplexer<EventListener> createUnmutableCopy()
	{
		EventListener eventListener= createListenerMultiplexer(proxiedClass);

		ProxyBasedListenerMultiplexer invocationHandler= (ProxyBasedListenerMultiplexer) Proxy.getInvocationHandler(eventListener);

		List<T> list= collectionHandler.getList();
		list.forEach(t -> invocationHandler.add(t));
		
		return (ListenerMultiplexer<EventListener>) eventListener;
	}

}
