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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.helpers.TimeCollector;
import com.dragome.helpers.jdbc.ResultSetProcessorExecutor;
import com.dragome.services.interfaces.ParametersHandler;
import com.dragome.services.interfaces.ReflectionService;
import com.dragome.services.interfaces.SerializationService;
import com.dragome.services.interfaces.ServiceFactory;
import com.dragome.services.serialization.FlexJsonSerializationService;

public class ServiceLocator
{
	protected static ServiceLocator instance;
	public static ServiceLocator getInstance()
	{
		if (instance == null)
			instance= new ServiceLocator();

		return instance;
	}

	protected boolean localExecution= false;
	protected TimeCollector timeCollector;
	protected SerializationService serializationService;
	protected MetadataManager metadataManager;
	protected ExecutorService fixedThreadPool;
	protected DragomeConfigurator configurator;
	protected ReflectionService reflectionService;
	protected ResultSetProcessorExecutor resultSetProcessorExecutor;
	protected ParametersHandler parametersHandler;
	protected ServiceFactory serviceFactory;

	public ServiceLocator()
	{
		serializationService= new FlexJsonSerializationService();
		reflectionService= new ReflectionServiceImpl();
		configurator= reflectionService.getConfigurator();
		fixedThreadPool= Executors.newCachedThreadPool();
		timeCollector= new TimeCollector();
		metadataManager= new MetadataManager();
		serviceFactory= new LocalServiceFactory();
	}

	public ReflectionService getReflectionService()
	{
		return reflectionService;
	}

	public ResultSetProcessorExecutor getResultSetProcessorExecutor()
	{
		return resultSetProcessorExecutor;
	}

	public ParametersHandler getParametersHandler()
	{
		return parametersHandler;
	}

	public SerializationService getSerializationService()
	{
		return serializationService;
	}

	public ServiceFactory getServiceFactory()
	{
		return serviceFactory;
	}

	public TimeCollector getTimeCollector()
	{
		return timeCollector;
	}

	public MetadataManager getMetadataManager()
	{
		return metadataManager;
	}

	public ExecutorService getExecutorService()
	{
		return fixedThreadPool;
	}

	public void setConfigurator(DragomeConfigurator configurator)
	{
		this.configurator= configurator;
	}

	public DragomeConfigurator getConfigurator()
	{
		return configurator;
	}

	public boolean isLocalExecution()
	{
		return localExecution;
	}

	public void setLocalExecution(boolean localExecution)
	{
		this.localExecution= localExecution;
	}

	public void setTimeCollector(TimeCollector timeCollector)
	{
		this.timeCollector= timeCollector;
	}

	public void setSerializationService(SerializationService serializationService)
	{
		this.serializationService= serializationService;
	}

	public void setMetadataManager(MetadataManager metadataManager)
	{
		this.metadataManager= metadataManager;
	}

	public void setReflectionService(ReflectionService reflectionService)
	{
		this.reflectionService= reflectionService;
	}

	public void setResultSetProcessorExecutor(ResultSetProcessorExecutor resultSetProcessorExecutor)
	{
		this.resultSetProcessorExecutor= resultSetProcessorExecutor;
	}

	public void setParametersHandler(ParametersHandler parametersHandler)
	{
		this.parametersHandler= parametersHandler;
	}

	public void setServiceFactory(ServiceFactory serviceFactory)
	{
		this.serviceFactory= serviceFactory;
	}
}
