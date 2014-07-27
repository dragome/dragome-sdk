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
package com.dragome.services;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dragome.commons.ContinueReflection;
import com.dragome.remote.entities.DragomeEntityManager;

public class ServiceInvocation
{
	private Class<?> type;
	private Method method;
	private List<?> args;
	private String id;
	private static Map<Class<?>, Object> services= new HashMap<Class<?>, Object>();

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

	public ServiceInvocation(Class<?> type, Method method, List<?> list, String id)
	{
		this.type= type;
		this.method= method;
		this.args= list;
		this.id= id;
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
				service= ServiceLocator.getInstance().getMetadataManager().getImplementationForInterface(type).newInstance();
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

}
