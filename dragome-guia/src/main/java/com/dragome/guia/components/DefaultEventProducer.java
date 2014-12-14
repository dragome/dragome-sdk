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
package com.dragome.model;

import java.lang.reflect.Proxy;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import com.dragome.model.interfaces.EventProducer;
import com.dragome.model.interfaces.ListenerMultiplexer;
import com.dragome.model.listeners.ListenerChanged;

public class DefaultEventProducer implements EventProducer
{
	protected Map<Class<? extends EventListener>, EventListener> listeners= new HashMap<Class<? extends EventListener>, EventListener>();
	protected Map<Object, EventListener> owners= new HashMap<Object, EventListener>();

	public DefaultEventProducer()
	{
		super();
	}

	public <T extends EventListener> void addListener(Class<T> aType, T aListener)
	{
		EventListener listener= listeners.get(aType);
		if (listener == null || (!(listener instanceof ListenerMultiplexer) && !Proxy.isProxyClass(listener.getClass())))
		{
			listeners.put(aType, listener= ProxyBasedListenerMultiplexer.createListenerMultiplexer(aType));
		}
		ListenerMultiplexer<T> listenerMultiplexer= (ListenerMultiplexer<T>) listener;
		listenerMultiplexer.add(aListener); //TODO compilador

		if (!aType.equals(ListenerChanged.class))
			if (hasListener(ListenerChanged.class))
				getListener(ListenerChanged.class).listenerAdded(aType, aListener);
	}

	public <T extends EventListener> void removeListener(Class<T> aType, T aListener)
	{
		EventListener listener= listeners.get(aType);
		if (listener instanceof ListenerMultiplexer)
		{
			ListenerMultiplexer<T> listenerMultiplexer= (ListenerMultiplexer<T>) listener;
			listenerMultiplexer.remove(aListener);
		}
		else
		{
			listeners.remove(aType);
		}

		if (!aType.equals(ListenerChanged.class))
			if (hasListener(ListenerChanged.class))
				getListener(ListenerChanged.class).listenerRemoved(aType, aListener);
	}

	public <T extends EventListener> boolean hasListener(Class<T> aType)
	{
		return listeners.containsKey(aType);
	}

	public <T extends EventListener> T getListener(Class<T> aType)
	{
		return (T) listeners.get(aType);
	}

	public <T extends EventListener> void addListenerForOwner(Class<T> aType, T aListener, Object owner)
	{
		//	T eventListener= (T) owners.get(owner);
		//	if (eventListener != null)
		//	{
		//	    removeListener(aType, eventListener);
		//	    owners.remove(owner);
		//	}
		//	
		//	owners.put(owner, aListener);
		addListener(aType, aListener);
	}
}
