package com.dragome.services;

import com.dragome.services.interfaces.AsyncServiceExecutor;
import com.dragome.services.interfaces.ServiceFactory;

public class LocalServiceFactory implements ServiceFactory
{
	public <T> T createSyncService(Class<? extends T> type)
	{
		ServiceLocator serviceLocator= ServiceLocator.getInstance();
		Class<?> implementationForInterface= serviceLocator.getMetadataManager().getImplementationForInterface(type);
		return serviceLocator.getReflectionService().createClassInstance((Class<? extends T>) implementationForInterface);
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
