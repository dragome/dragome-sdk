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

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.html.MessageEvent;
import org.w3c.dom.websocket.WebSocket;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.helpers.Base64Coder;
import com.dragome.web.annotations.ClientSideMethod;
import com.dragome.web.dispatcher.EventDispatcherHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.MessageEventExtension;

public class ClientToServerMessageChannel implements MessageChannel
{
	public static final String WEBSOCKET_PATH= "null";
	private static Receiver receiver;
	private int counter= 0;
	private Sender sender;
	private WebSocket websocket;

	public ClientToServerMessageChannel()
	{
		websocket= createWebsocket();
	}

	public void send(String aMessage)
	{
		String data= aMessage + "|" + counter;
		//		ScriptHelper.put("messageAsString", data, this);
		//		Object result= ScriptHelper.eval("new TextEncoder().encode(messageAsString)", this);
		websocket.send(data);
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

	public WebSocket createWebsocket()
	{
		ScriptHelper.put("s", "/dragome-debug", this);
		String url= (String) ScriptHelper.eval("((window.location.protocol === \"https:\") ? \"wss://\" : \"ws://\") + window.location.host + (location.pathname).substr(0, (location.pathname).lastIndexOf('/')) + s", this);
		ScriptHelper.put("url", url, this);
		WebSocket webSocket= ScriptHelper.evalCasting("new WebSocket(url)", WebSocket.class, this);
		//		webSocket.setBinaryType("arraybuffer");
		webSocket.setOnopen(new EventListener()
		{
			@ClientSideMethod
			public void handleEvent(Event evt)
			{
				EventDispatcherHelper.runApplication();
				System.out.println("open");
			}
		});

		webSocket.setOnmessage(new EventListener()
		{
			@ClientSideMethod
			public void handleEvent(Event evt)
			{
				try
				{
					MessageEventExtension messageEvent= JsCast.castTo(evt, MessageEventExtension.class);
					String dataAsString= messageEvent.getDataAsString();
					//					Object dataAsString= messageEvent.getDataAsObject();

					ScriptHelper.put("encodedData", dataAsString, this);
					//					String result= (String) ScriptHelper.eval("new TextDecoder().decode(dataAsString.node)", this);

					//					String result= (String) ScriptHelper.eval("new TextReader(new Utf8Translator(new Inflator(new Base64Reader(encodedData)))).readToEnd()", this);
					Object result= ScriptHelper.eval("Uint8Array.from(atob(encodedData), c => c.charCodeAt(0))", this);
					//					String result= dataAsString;
					ScriptHelper.put("resultArray", result, this);

					byte[] byteArray= new byte[1000];

					for (int i= 0; i < 600; i++)
					{
						ScriptHelper.put("index", i, this);
						int item= ScriptHelper.evalInt("resultArray[index]", this);
						byteArray[i]= (byte) item;
					}

					FSTConfiguration conf= FSTConfiguration.getDefaultConfiguration();
					conf.setForceSerializable(false);
					conf.registerSerializer(Method.class, new MethodSerializer(), false);

					FSTObjectInput fstObjectInput= new FSTObjectInput(new ByteArrayInputStream(byteArray));
					Object readObject= fstObjectInput.readObject();

					receiver.messageReceived((String) readObject);
					evt.stopPropagation();
				}
				catch (Exception e)
				{
				}
			}
		});
		webSocket.setOnclose(new EventListener()
		{
			@ClientSideMethod
			public void handleEvent(Event evt)
			{
				System.out.println("close");
			}
		});

		return webSocket;
	}
}
