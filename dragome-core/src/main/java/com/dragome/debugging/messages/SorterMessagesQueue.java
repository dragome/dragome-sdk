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
package com.dragome.debugging.messages;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;

public class SorterMessagesQueue implements Runnable
{
	private Map<Integer, String> recievedMessages= new Hashtable<Integer, String>();
	public int lastMessageId= 0;

	public SorterMessagesQueue()
	{
	}

	public void clear()
	{
		recievedMessages.clear();
		lastMessageId= 0;
	}

	public void addMessage(int index, String message)
	{
		recievedMessages.put(index, message);
		//	    System.out.println("message:" + index);
	}

	public void run()
	{
		while (true)
		{
			String currentMessage= null;
			do
			{
				currentMessage= recievedMessages.get(lastMessageId);
				if (currentMessage != null)
				{
					lastMessageId++;

					final String finalMessage= currentMessage;

					ExecutorService executorService= ServiceLocator.getInstance().getExecutorService();
					executorService.execute(new Runnable()
					{
						public void run()
						{
							ServiceInvocation serviceInvocation= (ServiceInvocation) ServiceLocator.getInstance().getSerializationService().deserialize(finalMessage);
							serviceInvocation.invoke();
						}
					});
				}
			}
			while (currentMessage != null);
		}
	}
}
