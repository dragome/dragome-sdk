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
package com.dragome.services;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dragome.commons.ContinueReflection;
import com.dragome.helpers.DragomeEntityManager;

public class ServiceInvocation
{
	private Class<?> type;
	private Method method;
	private List<?> args;
	private String id;
	private static Map<Class<?>, Object> services= new HashMap<Class<?>, Object>();
	private boolean voidService;

	public void setId(String id)
	{
		this.id= id;
	}

	public ServiceInvocation()
	{
	}

	public ServiceInvocation(Class<?> type, Method method, List<?> list)
	{
		this.type= type;
		this.method= method;
		this.args= list;
		id= DragomeEntityManager.getEntityId(this);
	}

	public ServiceInvocation(Class<?> type, Method method, List<?> list, String id, boolean voidService)
	{
		this.type= type;
		this.method= method;
		this.args= list;
		this.id= id;
		this.voidService= voidService;
	}

	public List<?> getArgs()
	{
		return args;
	}

	public Method getMethod()
	{
		return method;
	}

	public Class<?> getType()
	{
		return type;
	}

	public void setArgs(List<?> list)
	{
		this.args= list;
	}
	public void setMethod(Method method)
	{
		this.method= method;
	}

	public void setType(Class<?> type)
	{
		this.type= type;
	}

	@ContinueReflection
	public Object invoke()
	{
		try
		{
			Object service= services.get(type);
			if (service == null)
			{
				Class<?> implementationForInterface= ServiceLocator.getInstance().getMetadataManager().getImplementationForInterface(type);
				service= implementationForInterface.newInstance();
				services.put(type, service);
//				System.out.println("service " + type.getName() + " created");
			}

			Object[] array= args.toArray();
			Method localMethod= method;
			
			return localMethod.invoke(service, array);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	public String getId()
	{
		return id;
	}

	public boolean isVoidService()
	{
		return voidService;
	}

	public void setVoidService(boolean voidService)
	{
		this.voidService = voidService;
	}

}
