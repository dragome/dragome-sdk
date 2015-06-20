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

import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;

public class ClientToServerMessageChannel implements MessageChannel
{
	public static final String WEBSOCKET_PATH= "null";
	private static Receiver receiver;
	private int counter= 0;
	private Sender sender;

	public ClientToServerMessageChannel()
	{
		ScriptHelper.put("url", WEBSOCKET_PATH, null);
		ScriptHelper.eval("this.socket= socketCreator(url,_ed.webSocketSuccessCallback,_ed.webSocketErrorCallback)", null);
	}

	@MethodAlias(alias= "_ed.webSocketErrorCallback")
	private static void errorCallback()
	{
		receiver.messageReceived("error!!!");
	}

	@MethodAlias(alias= "_ed.webSocketSuccessCallback")
	public static void successCallback(String message)
	{
		if (message != null && message.trim().length() > 0)
			receiver.messageReceived(message);
	}

	public void send(String aMessage)
	{
		ScriptHelper.put("message", aMessage, null);
		ScriptHelper.put("counter", counter, null);
		ScriptHelper.eval("window.subSocket.push(message+'|'+counter)", null);
		counter++;
	}

	public void setReceiver(Receiver receiver)
	{
		ClientToServerMessageChannel.receiver= receiver;
	}

	public Receiver getReceiver()
	{
		return receiver;
	}

	public void setSender(Sender sender)
	{
		this.sender= sender;
	}
}
