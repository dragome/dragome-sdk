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

import java.util.ArrayList;
import java.util.List;

import com.dragome.helpers.DragomeEntityManager;
import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;
import com.dragome.services.interfaces.ReflectionService;
import com.dragome.web.debugging.JavascriptReference;
import com.dragome.web.enhancers.jsdelegate.JsCast;

public class EventDispatcherImpl implements EventDispatcher
{
	private static boolean processing;

	public EventDispatcherImpl()
	{
	}

	public void callJavaMethod(ServiceInvocation serviceInvocation)
	{
		if (!processing)
		{
			processing= true;
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

						Object createFromNode= JsCast.castTo(javascriptReference, referenceType);
						effectiveParameters.add(createFromNode);
					}
					else
						effectiveParameters.add(object);
				}

				serviceInvocation.getMethod().setAccessible(true);
				serviceInvocation.getMethod().invoke(DragomeEntityManager.get(serviceInvocation.getId()), effectiveParameters.toArray());
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				processing= false;
			}
		}
		else
			throw new CannotExecuteJavaMethod();
	}
}
