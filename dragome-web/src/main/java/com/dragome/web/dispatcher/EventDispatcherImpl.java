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
package com.dragome.web.dispatcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.events.EventListener;

import com.dragome.helpers.DragomeEntityManager;
import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;
import com.dragome.services.interfaces.ReflectionService;
import com.dragome.web.debugging.JavascriptReference;
import com.dragome.web.enhancers.jsdelegate.JsDelegateFactory;
import com.dragome.web.html.dom.w3c.KeyboardEventImpl;
import com.dragome.web.html.dom.w3c.MouseEventImpl;

public class EventDispatcherImpl implements EventDispatcher
{
	public static final String ELEMENT_ID_ATTRIBUTE= "data-element-id";

	public EventDispatcherImpl()
	{
	}

	public static void setEventListener(Element element, EventListener eventListener, String... eventTypes)
	{
		for (String eventType : eventTypes)
			element.setAttribute("on" + eventType, "_ed.onEvent()");

		element.setAttribute(ELEMENT_ID_ATTRIBUTE, DragomeEntityManager.add(eventListener));
	}

	public static void removeEventListener(Element element, String... eventTypes)
	{
		for (String eventType : eventTypes)
			element.removeAttribute("on" + eventType);

		element.removeAttribute(ELEMENT_ID_ATTRIBUTE);
	}

	public static void removeEventListener(Element element, EventListener eventListener, String... eventTypes)
	{
		removeEventListener(element, eventTypes);
		DragomeEntityManager.remove(eventListener);
	}

	public void keyEventPerformedById(final String eventName, final String id, final int code)
	{
		EventDispatcherHelper.runOnlySynchronized(new Runnable()
		{
			public void run()
			{
				Object object= DragomeEntityManager.get(id);
				if (object instanceof EventListener)
					((EventListener) object).handleEvent(new KeyboardEventImpl(eventName, code));
			}
		});
	}

	public void mouseEventPerformedById(final String eventName, final String id, final int clientX, final int clientY, final boolean shiftKey)
	{
		EventDispatcherHelper.runOnlySynchronized(new Runnable()
		{
			public void run()
			{
				Object object= DragomeEntityManager.get(id);
				if (object instanceof EventListener)
					((EventListener) object).handleEvent(new MouseEventImpl(eventName, clientX, clientY, shiftKey));
			}
		});
	}

	public void callJavaMethod(ServiceInvocation serviceInvocation)
	{
		try
		{
			ReflectionService reflectionService= ServiceLocator.getInstance().getReflectionService();
			List<Object> effectiveParameters= new ArrayList<>();
			for (Object object : serviceInvocation.getArgs())
			{
				if (object instanceof JavascriptReference)
				{
					JavascriptReference javascriptReference= (JavascriptReference) object;
					Class<?> referenceType= reflectionService.forName(javascriptReference.getClassName());

					Object createFromNode= JsDelegateFactory.createFromNode(javascriptReference, referenceType);
					effectiveParameters.add(createFromNode);
				}
				else
					effectiveParameters.add(object);
			}

			serviceInvocation.getMethod().invoke(DragomeEntityManager.get(serviceInvocation.getId()), effectiveParameters.toArray());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
