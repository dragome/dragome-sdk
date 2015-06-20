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

public class ServerToClientMessageChannel implements MessageChannel
{
	private Receiver receiver;
	private Sender sender;

	public Receiver getReceiver()
	{
		return receiver;
	}

	public void send(String aMessage)
	{
		if (sender != null)
			sender.send(aMessage);
	}

	public void setReceiver(Receiver receiver)
	{
		this.receiver= receiver;
	}

	public void setSender(Sender sender)
	{
		this.sender= sender;
	}
}
