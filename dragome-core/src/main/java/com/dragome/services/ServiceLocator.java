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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.commons.compiler.BytecodeToJavascriptCompiler;
import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.debugging.messages.ClientToServerMessageChannel;
import com.dragome.debugging.messages.MessageChannel;
import com.dragome.debugging.messages.ServerToClientMessageChannel;
import com.dragome.dispatcher.EventDispatcher;
import com.dragome.dispatcher.EventDispatcherImpl;
import com.dragome.helpers.TimeCollector;
import com.dragome.helpers.jdbc.BrowserResultSetProcessorExecutor;
import com.dragome.helpers.jdbc.NullResultSetProcessorExecutor;
import com.dragome.helpers.jdbc.ResultSetProcessorExecutor;
import com.dragome.html.dom.DomHandler;
import com.dragome.html.dom.w3c.BrowserDomHandler;
import com.dragome.services.interfaces.ParametersHandler;
import com.dragome.services.interfaces.ReflectionService;
import com.dragome.services.interfaces.RequestExecutor;
import com.dragome.services.interfaces.SerializationService;
import com.dragome.services.interfaces.ServiceFactory;
import com.dragome.services.serialization.FlexJsonSerializationService;
import com.dragome.services.serverside.ServerReflectionServiceImpl;

public class ServiceLocator
{
	protected static ServiceLocator instance;
 
	public static ServiceLocator getInstance()
	{
		if (instance == null)
			instance= new ServiceLocator();

		return instance;
	}

	protected boolean clientSideEnabled= false;

	protected DomHandler domHandler;
	protected TimeCollector timeCollector;
	protected MessageChannel serverToClientMessageChannel;
	protected MessageChannel clientToServerMessageChannel;
	protected SerializationService serializationService;
	protected MetadataManager metadataManager;
	protected ExecutorService fixedThreadPool;
	protected DragomeConfigurator configurator;

	public boolean isClientSideEnabled()
	{
		return clientSideEnabled;
	}

	@MethodAlias(alias= "this.setClientSideEnabled")
	public void setClientSideEnabled(boolean clientSideEnabled)
	{
		this.clientSideEnabled= clientSideEnabled;
	}

	public ReflectionService getReflectionService()
	{
		if (isClientSide())
			return new ReflectionServiceImpl();
		else
			return new ServerReflectionServiceImpl();
	}

	public DomHandler getDomHandler()
	{
		if (domHandler == null)
			domHandler= new BrowserDomHandler();
		return domHandler;
	}
	public void setDomHandler(DomHandler domHandler)
	{
		this.domHandler= domHandler;
	}

	public ResultSetProcessorExecutor getResultSetProcessorExecutor()
	{
		if (!isClientSide())
			return new NullResultSetProcessorExecutor();
		else
			return new BrowserResultSetProcessorExecutor();
	}

	public ParametersHandler getParametersHandler()
	{
		//	if (isClientSide())
		return new BrowserParametersHandler();
		//	else
		//	    return new CommandLineParametersHandler();
	}

	public SerializationService getSerializationService()
	{
		if (serializationService == null)
			serializationService= new FlexJsonSerializationService();

		return serializationService;
	}

	public ServiceFactory getServiceFactory()
	{
		return getClientSideServiceFactory();
	}

	public ServiceFactory getClientSideServiceFactory()
	{
		return new ClientSideServiceFactory();
	}

	public ServiceFactory getServerSideServiceFactory()
	{
		return new ServerSideServiceFactory();
	}

	public TimeCollector getTimeCollector()
	{
		if (timeCollector == null)
			timeCollector= new TimeCollector();

		return timeCollector;
	}

	public EventDispatcher getEventDispatcher()
	{
		if (isRemoteDebugging())
			return getClientSideServiceFactory().createAsyncService(EventDispatcher.class).getService();
		else
			return new EventDispatcherImpl();
	}

	public boolean isRemoteDebugging()
	{
		return Boolean.parseBoolean(getParametersHandler().getParameter("debug"));
	}

	public boolean isClientSide()
	{
		return clientSideEnabled;
	}

	public boolean isMethodVoid(Method method)
	{
		return method.getReturnType().equals(Void.class) || method.getReturnType().equals(void.class);
	}

	public MessageChannel getServerToClientMessageChannel()
	{
		if (serverToClientMessageChannel == null)
			serverToClientMessageChannel= new ServerToClientMessageChannel();

		return serverToClientMessageChannel;
	}

	public void setMessageChannelToClientSide(MessageChannel messageChannel)
	{
		serverToClientMessageChannel= messageChannel;
	}

	public MessageChannel getClientToServerMessageChannel()
	{
		if (clientToServerMessageChannel == null)
			clientToServerMessageChannel= new ClientToServerMessageChannel();

		return clientToServerMessageChannel;
	}

	public MetadataManager getMetadataManager()
	{
		if (metadataManager == null)
			metadataManager= new MetadataManager();

		return metadataManager;
	}

	public ExecutorService getExecutorService()
	{
		if (fixedThreadPool == null)
			fixedThreadPool= Executors.newCachedThreadPool();
		return fixedThreadPool;
	}

	public void setConfigurator(DragomeConfigurator configurator)
	{
		this.configurator= configurator;
	}

	public DragomeConfigurator getConfigurator()
	{
		try
		{
			if (configurator == null && !isClientSide())
				configurator= getReflectionService().getConfigurator();

			return configurator;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public RequestExecutor getRequestExecutor()
	{
		return new RequestExecutorImpl(false);
	}

	public BytecodeToJavascriptCompiler getBytecodeToJavascriptCompiler()
	{
		ReflectionService reflectionService= getReflectionService();
		Class<BytecodeToJavascriptCompiler> clazz= (Class<BytecodeToJavascriptCompiler>) reflectionService.getSubTypesOf(BytecodeToJavascriptCompiler.class).iterator().next();
		return reflectionService.createClassInstance(clazz);
	}
}
