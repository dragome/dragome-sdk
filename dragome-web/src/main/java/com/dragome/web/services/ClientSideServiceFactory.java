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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.dragome.services.interfaces.AsyncCallback;
import com.dragome.services.interfaces.AsyncServiceExecutor;
import com.dragome.services.interfaces.RequestExecutor;
import com.dragome.services.interfaces.SerializationService;
import com.dragome.services.interfaces.ServiceFactory;

public class ClientSideServiceFactory implements ServiceFactory
{
	public <T> T createSyncService(Class<? extends T> type)
	{
		return createProxy(type, new AbstractServicesInvocationHandler(type)
		{
			public Object execute(Map<String, String> parameters, RequestExecutor requestExecutor, SerializationService serializationService, boolean returnsValue)
			{
				String result= requestExecutor.executeSynchronousRequest(AbstractServicesInvocationHandler.SERVICE_INVOKER_PATH, parameters);
				return serializationService.deserialize(result);
			}
		});
	}

	public <T> T createFixedSyncService(Class<? extends T> type)
	{
		return createProxy(type, new AbstractServicesInvocationHandler(type)
		{
			public Object execute(Map<String, String> parameters, RequestExecutor requestExecutor, SerializationService serializationService, boolean returnsValue)
			{
				String result= requestExecutor.executeFixedSynchronousRequest(AbstractServicesInvocationHandler.SERVICE_INVOKER_PATH, parameters);
				return serializationService.deserialize(result);
			}
		});
	}

	public <T> AsyncServiceExecutor<T> createAsyncService(final Class<? extends T> type)
	{
		return new AsyncServiceExecutor<T>()
		{
			protected AsyncCallback<Object> finalAsyncCallback;
			protected AsyncCallback<String> serviceAsyncCallback;

			protected RequestExecutor requestExecutor1;
			protected Map<String, String> parameters1;

			final T createProxy= createProxy(type, new AbstractServicesInvocationHandler(type)
			{
				public Object execute(Map<String, String> parameters, RequestExecutor requestExecutor, final SerializationService serializationService, boolean returnsValue)
				{
					parameters1= parameters;
					requestExecutor1= requestExecutor;

					AsyncCallback<String> asyncCallback= new AsyncCallback<String>()
					{
						public void onSuccess(String result)
						{
							if (result != null && result.trim().length() > 0 && finalAsyncCallback != null)
								finalAsyncCallback.onSuccess(serializationService.deserialize(result));
						}

						public void onError()
						{
							throw new RuntimeException("async call error");
						}
					};

					serviceAsyncCallback= RequestExecutorImpl.wrapCallback(Serializable.class, asyncCallback);

					if (!returnsValue)
						requestExecutor1.executeAsynchronousRequest(AbstractServicesInvocationHandler.SERVICE_INVOKER_PATH, parameters1, serviceAsyncCallback);

					return null;
				}
			});

			public T getService()
			{
				return createProxy;
			}

			public <S> void executeAsync(S value, AsyncCallback<S> asyncCallback)
			{
				finalAsyncCallback= (AsyncCallback<Object>) asyncCallback;
				requestExecutor1.executeAsynchronousRequest(AbstractServicesInvocationHandler.SERVICE_INVOKER_PATH, parameters1, serviceAsyncCallback);
			}
		};

	}

	private <T> T createProxy(final Class<T> type, InvocationHandler invocationHandler)
	{
		return (T) Proxy.newProxyInstance(ClientSideServiceFactory.class.getClassLoader(), new Class[] { type }, invocationHandler);
	}
}
