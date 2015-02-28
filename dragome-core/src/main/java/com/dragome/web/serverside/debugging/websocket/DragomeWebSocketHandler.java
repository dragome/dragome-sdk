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
package com.dragome.web.serverside.debugging.websocket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.websocket.WebSocket;

import com.dragome.services.ServiceLocator;
import com.dragome.web.debugging.messages.ChannelReceiverImpl;
import com.dragome.web.debugging.messages.Receiver;
import com.dragome.web.debugging.messages.Sender;

public class DragomeWebSocketHandler
{
	private static Logger LOGGER= Logger.getLogger(DragomeWebSocketHandler.class.getName());
	public static final String DRAGOME_WEBSOCKET= "/dragome-websocket";

	public void onClose(WebSocket webSocket)
	{
	};

	public void onOpen(WebSocket webSocket) throws IOException
	{
		final Broadcaster broadcaster= webSocket.resource().getBroadcaster();
		Receiver configuredReceiver= ServiceLocator.getInstance().getServerToClientMessageChannel().getReceiver();
		if (!(configuredReceiver instanceof ChannelReceiverImpl))
			ServiceLocator.getInstance().getServerToClientMessageChannel().setReceiver(new ChannelReceiverImpl());
		else
			configuredReceiver.reset();

		ServiceLocator.getInstance().getServerToClientMessageChannel().setSender(new Sender()
		{
			public void send(String aMessage)
			{
				broadcaster.broadcast(aMessage);
			}
		});

		ServiceLocator.getInstance().getServerToClientMessageChannel().getReceiver().reset();

		webSocket.resource().setBroadcaster(BroadcasterFactory.getDefault().lookup(DRAGOME_WEBSOCKET, true));
		webSocket.resource().addEventListener(new AtmosphereResourceEventListenerAdapter()
		{
			public void onDisconnect(AtmosphereResourceEvent event)
			{
				if (event.isCancelled())
				{
					LOGGER.log(Level.INFO, "Browser {0} unexpectedly disconnected", event.getResource().uuid());
				}
				else if (event.isClosedByClient())
				{
					LOGGER.log(Level.INFO, "Browser {0} closed the connection", event.getResource().uuid());
				}
			}
		});
	}
	public void onByteMessage(WebSocket webSocket, byte[] data, int offset, int length) throws IOException
	{
	}

	public void onTextMessage(WebSocket webSocket, String message) throws IOException
	{
		ServiceLocator.getInstance().getServerToClientMessageChannel().getReceiver().messageReceived(message);
	}
}
