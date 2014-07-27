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

import java.util.concurrent.ExecutorService;

import com.dragome.debugging.messages.ServerToClientServiceInvoker;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.services.interfaces.AsyncResponseHandler;

public class AsyncResponseHandlerImpl implements AsyncResponseHandler
{
	public AsyncResponseHandlerImpl()
	{
	}

	public void pushResponse(final String result, String id)
	{
		if (id != null /*&& !result.equals("null")*/)  //TODO: posible cuelgue de threads
		{
			final AsyncCallbackWrapper<String> asyncCallback= (AsyncCallbackWrapper<String>) DragomeEntityManager.get(id);

			ExecutorService executorService= ServiceLocator.getInstance().getExecutorService();
			executorService.execute(new Runnable()
			{
				public void run()
				{
					asyncCallback.getAsyncCallback().onSuccess(result);
					ServerToClientServiceInvoker.finalizeMethodInvocationsInClient();
				}
			});
		}
	}
}
