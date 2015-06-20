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
package com.dragome.services.serverside;

import java.lang.reflect.Proxy;

import com.dragome.services.interfaces.AsyncServiceExecutor;
import com.dragome.services.interfaces.ServiceFactory;
import com.dragome.web.services.ClientServiceInvocationHandler;

public class ServerSideServiceFactory implements ServiceFactory
{
	public <T> T createSyncService(Class<? extends T> type)
	{
		return (T) Proxy.newProxyInstance(ServerSideServiceFactory.class.getClassLoader(), new Class[] { type }, new ClientServiceInvocationHandler(type));
	}

    public <T> T createFixedSyncService(Class<? extends T> type)
    {
		return createSyncService(type);
    }

    public <T> AsyncServiceExecutor<T> createAsyncService(Class<? extends T> type)
    {
	    return null;
    }
}
