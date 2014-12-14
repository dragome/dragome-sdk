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
package com.dragome.services;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.services.interfaces.RequestExecutor;
import com.dragome.services.interfaces.SerializationService;

public abstract class AbstractServicesInvocationHandler extends AbstractProxyRelatedInvocationHandler
{
	public static final String INVOCATION= "invocation";
	public static final String SERVICE_INVOKER_PATH= "service-invoker";

	protected Class<?> type;

	public AbstractServicesInvocationHandler(Class<?> type)
	{
		this.type= type;
	}

	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
	{
		setProxy(proxy);

		final ServiceLocator instance= ServiceLocator.getInstance();
		Map<String, String> parameters= createParameters(type, method, args);
		RequestExecutor requestExecutor= instance.getRequestExecutor();
		final SerializationService serializationService= instance.getSerializationService();

		return execute(parameters, requestExecutor, serializationService, !method.getReturnType().equals(void.class));
	}

	public static Map<String, String> createParameters(Class<?> type, Method method, Object[] args)
	{
		List<Object> asList= args != null ? Arrays.asList(args) : new ArrayList<Object>();
		ServiceInvocation serviceInvocation= new ServiceInvocation(type, method, asList);
		String serialize= ServiceLocator.getInstance().getSerializationService().serialize(serviceInvocation);
		HashMap<String, String> parameters= new HashMap<String, String>();
		parameters.put(INVOCATION, serialize);
		return parameters;
	}

	abstract Object execute(Map<String, String> parameters, RequestExecutor requestExecutor, SerializationService serializationService, boolean returnsValue);
}
