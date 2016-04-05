package com.dragome.web.serverside.debugging.websocket;

import java.io.IOException;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.dragome.services.WebServiceLocator;
import com.dragome.web.debugging.messages.ChannelReceiverImpl;
import com.dragome.web.debugging.messages.Receiver;
import com.dragome.web.debugging.messages.Sender;

@ServerEndpoint(value= "/dragome-debug")
public class DragomeDebugServerEndpoint
{
	private Logger logger= Logger.getLogger(this.getClass().getName());

	@OnOpen
	public void onOpen(final Session session)
	{
		Receiver configuredReceiver= WebServiceLocator.getInstance().getServerToClientMessageChannel().getReceiver();
		if (!(configuredReceiver instanceof ChannelReceiverImpl))
			WebServiceLocator.getInstance().getServerToClientMessageChannel().setReceiver(new ChannelReceiverImpl());
		else
			configuredReceiver.reset();

		WebServiceLocator.getInstance().getServerToClientMessageChannel().setSender(new Sender()
		{
			public void send(String aMessage)
			{
				try
				{
					session.getBasicRemote().sendText(aMessage);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		});

		WebServiceLocator.getInstance().getServerToClientMessageChannel().getReceiver().reset();
	}

	@OnMessage
	public String onMessage(String message, Session session)
	{
		WebServiceLocator.getInstance().getServerToClientMessageChannel().getReceiver().messageReceived(message);
		return message;
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason)
	{
		logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
	}
}