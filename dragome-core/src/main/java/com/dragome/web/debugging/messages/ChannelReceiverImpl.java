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
package com.dragome.web.debugging.messages;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.services.ServiceLocator;
import com.dragome.web.debugging.RemoteScriptHelper;

public final class ChannelReceiverImpl implements Receiver
{
	private SorterMessagesQueue queue= new SorterMessagesQueue();

	{
		ServiceLocator.getInstance().getExecutorService().execute(queue);
	}

	public void messageReceived(String aMessage)
	{
		//		System.out.println("received:" + message);
		int separatorIndex= aMessage.lastIndexOf("|");
		queue.addMessage(Integer.parseInt(aMessage.substring(separatorIndex+1)), aMessage);
	}
	public void reset()
	{
		ScriptHelper.scriptHelperInterface= new RemoteScriptHelper(ServiceLocator.getInstance().getServerSideServiceFactory());
		queue.clear();
		DragomeEntityManager.clear();
	}
}
