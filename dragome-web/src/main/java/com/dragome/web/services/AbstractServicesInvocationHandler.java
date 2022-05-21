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
package com.dragome.web.services;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
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
		setInterfaces(Arrays.asList(type));
	}

	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
	{
		if (method.getName().equals("hashCode"))
			return type.hashCode();
		else if (method.getName().equals("equals"))
			return super.equals(args[0]);
		else
		{
			setProxy(proxy);

			final WebServiceLocator webServiceLocator= WebServiceLocator.getInstance();
			final ServiceLocator instance= ServiceLocator.getInstance();

			Map<String, String> parameters= createParameters(type, method, args);
			RequestExecutor requestExecutor= webServiceLocator.getRequestExecutor();
			final SerializationService serializationService= instance.getSerializationService();

			return execute(parameters, requestExecutor, serializationService, !method.getReturnType().equals(void.class));
		}
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

	public abstract Object execute(Map<String, String> parameters, RequestExecutor requestExecutor, SerializationService serializationService, boolean returnsValue);
}
