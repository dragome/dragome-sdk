package com.dragome.web.serverside.debugging.websocket;

import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import com.dragome.helpers.DragomeEntityManager;
import com.dragome.services.WebServiceLocator;
import com.dragome.web.debugging.CrossExecutionSemaphore;
import com.dragome.web.debugging.messages.ChannelReceiverImpl;
import com.dragome.web.debugging.messages.Receiver;
import com.dragome.web.debugging.messages.Sender;
import com.dragome.web.html.dom.w3c.CombinedDomInstance;

public class DragomeDebugServerEndpoint
{
	private Logger logger= Logger.getLogger(this.getClass().getName());
	private Basic remote;

	public void onOpen(final Session session)
	{
		Receiver configuredReceiver= WebServiceLocator.getInstance().getServerToClientMessageChannel().getReceiver();
		if (!(configuredReceiver instanceof ChannelReceiverImpl))
			WebServiceLocator.getInstance().getServerToClientMessageChannel().setReceiver(new ChannelReceiverImpl());
		else
			configuredReceiver.reset();

		WebServiceLocator.getInstance().getDomHandler().reset();
		DragomeEntityManager.clear();
		CombinedDomInstance.reset();
		CrossExecutionSemaphore.reset();

		if (session != null && session.isOpen())
		{
			//			remote= session.getAsyncRemote();
			remote= session.getBasicRemote();
		}

		WebServiceLocator.getInstance().getServerToClientMessageChannel().setSender(new Sender()
		{
			public void send(String aMessage)
			{
				try
				{
					if (session != null && session.isOpen())
						remote.sendText(aMessage);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		});

		WebServiceLocator.getInstance().getServerToClientMessageChannel().getReceiver().reset();
	}

	public String onMessage(String message, Session session)
	{
		//		ServiceInvocation serviceInvocation= (ServiceInvocation) ServiceLocator.getInstance().getSerializationService().deserialize(message);
		//		serviceInvocation.invoke();

		WebServiceLocator.getInstance().getServerToClientMessageChannel().getReceiver().messageReceived(message);

		return null;
	}

	public void onClose(Session session, CloseReason closeReason)
	{
		logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
	}
}