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

import com.dragome.commons.compiler.BytecodeToJavascriptCompiler;
import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.interfaces.ReflectionService;
import com.dragome.services.interfaces.RequestExecutor;
import com.dragome.services.interfaces.ServiceFactory;
import com.dragome.services.serverside.ServerReflectionServiceImpl;
import com.dragome.services.serverside.ServerSideServiceFactory;
import com.dragome.web.debugging.messages.ClientToServerMessageChannel;
import com.dragome.web.debugging.messages.MessageChannel;
import com.dragome.web.debugging.messages.ServerToClientMessageChannel;
import com.dragome.web.dispatcher.EventDispatcher;
import com.dragome.web.dispatcher.EventDispatcherImpl;
import com.dragome.web.html.dom.DomHandler;
import com.dragome.web.html.dom.w3c.BrowserDomHandler;
import com.dragome.web.services.BrowserParametersHandler;
import com.dragome.web.services.ClientSideServiceFactory;
import com.dragome.web.services.RequestExecutorImpl;

public class WebServiceLocator
{
	protected static WebServiceLocator instance= new WebServiceLocator();

	public static WebServiceLocator getInstance()
	{
		return instance;
	}

	protected boolean clientSideEnabled= false;
	protected DomHandler domHandler;
	protected MessageChannel serverToClientMessageChannel;
	protected MessageChannel clientToServerMessageChannel;

	public WebServiceLocator()
	{
		Object document= null;

		document= ScriptHelper.eval("window.document", this);

		clientSideEnabled= document != null;

		ServiceLocator.getInstance().setParametersHandler(new BrowserParametersHandler());

		init();
	}

	private void init()
	{
		ServiceLocator serviceLocator= ServiceLocator.getInstance();
		if (serviceLocator.isLocalExecution())
			serviceLocator.setServiceFactory(new LocalServiceFactory());
		else
			serviceLocator.setServiceFactory(getClientSideServiceFactory());

		if (isClientSide())
			serviceLocator.setReflectionService(new ReflectionServiceImpl());
		else
			serviceLocator.setReflectionService(new ServerReflectionServiceImpl());

		if (serviceLocator.getConfigurator() == null && !isClientSide())
			serviceLocator.setConfigurator(serviceLocator.getReflectionService().getConfigurator());
	}

	public boolean isClientSideEnabled()
	{
		return clientSideEnabled;
	}

	@MethodAlias(alias= "this.setClientSideEnabled")
	public void setClientSideEnabled(boolean clientSideEnabled)
	{
		this.clientSideEnabled= clientSideEnabled;
		init();
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

	public ServiceFactory getClientSideServiceFactory()
	{
		return new ClientSideServiceFactory();
	}

	public ServiceFactory getServerSideServiceFactory()
	{
		return new ServerSideServiceFactory();
	}

	public EventDispatcher getEventDispatcher()
	{
		if (isRemoteDebugging())
			return RequestExecutorImpl.createRemoteServiceByWebSocket(EventDispatcher.class);
		else
			return new EventDispatcherImpl();
	}

	public boolean isRemoteDebugging()
	{
		return Boolean.parseBoolean(ServiceLocator.getInstance().getParametersHandler().getParameter("debug"));
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

	public RequestExecutor getRequestExecutor()
	{
		return new RequestExecutorImpl(false);
	}

	public BytecodeToJavascriptCompiler getBytecodeToJavascriptCompiler()
	{
		ReflectionService reflectionService= ServiceLocator.getInstance().getReflectionService();
		Class<BytecodeToJavascriptCompiler> clazz= (Class<BytecodeToJavascriptCompiler>) reflectionService.getSubTypesOf(BytecodeToJavascriptCompiler.class).iterator().next();
		return reflectionService.createClassInstance(clazz);
	}
}
