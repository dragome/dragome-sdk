/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.web.services;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.EventHandler;
import org.w3c.dom.XMLHttpRequest;
import org.w3c.dom.events.Event;

import com.dragome.commons.DelegateCode;
import com.dragome.commons.ExecutionHandler;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.AsyncCallbackWrapper;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.services.interfaces.AsyncCallback;
import com.dragome.services.interfaces.AsyncResponseHandler;
import com.dragome.services.interfaces.RequestExecutor;
import com.dragome.web.debugging.messages.ClientToServerServiceInvocationHandler;

public class RequestExecutorImpl implements RequestExecutor
{
	public interface XMLHttpRequestExtension extends XMLHttpRequest
	{
		@DelegateCode(eval= "this.node.send()")
		public String sendSync();
		@DelegateCode(eval= "this.node.send($1)")
		public String sendSync(Object data);
	}

	private boolean useGetMethod;

	public RequestExecutorImpl(boolean useGetMethod)
	{
		this.useGetMethod= useGetMethod;
	}

	public String executeSynchronousRequest(String url, Map<String, String> parameters)
	{
		final ExecutionHandler executionHandler= ServiceLocator.getInstance().getConfigurator().getExecutionHandler();
		if (executionHandler.canSuspend())
		{
			final String[] resultArray= new String[] { "" };

			AsyncCallback<String> asyncCallbackToContinue= new AsyncCallback<String>()
			{
				public void onSuccess(String result)
				{
					resultArray[0]= result;
					executionHandler.continueExecution();
				}

				public void onError()
				{
					throw new RuntimeException("Error in async call");
				}
			};
			AsyncCallback<String> wrappedCallback= wrapCallback(Serializable.class, asyncCallbackToContinue);
			executeHttpRequest(true, url, parameters, wrappedCallback, false, useGetMethod);

			executionHandler.suspendExecution();
			return resultArray[0];
		}
		else
			return executeHttpRequest(false, url, parameters, null, false, useGetMethod);

	}

	public String executeFixedSynchronousRequest(String url, Map<String, String> parameters)
	{
		return executeHttpRequest(false, url, parameters, null, false, useGetMethod);
	}

	public static String executeHttpRequest(boolean isAsync, String url, Map<String, String> parameters, final AsyncCallback<String> asyncCallback, boolean crossDomain, boolean useGetMethod)
	{
		try
		{
			return executeHttpRequestInternal(isAsync, url, parameters, asyncCallback, useGetMethod);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private static String executeHttpRequestInternal(boolean isAsync, String url, Map<String, String> parameters, final AsyncCallback<String> asyncCallback, boolean useGetMethod) throws UnsupportedEncodingException
	{
		ScriptHelper.put("url", url, null);
		final XMLHttpRequestExtension xmlHttpRequest= ScriptHelper.evalCasting("new XMLHttpRequest()", XMLHttpRequestExtension.class, null);
		xmlHttpRequest.open(useGetMethod ? "GET" : "POST", url, isAsync);
		xmlHttpRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

		String parameter= "";

		if (parameters != null)
			for (Entry<String, String> entry : parameters.entrySet())
				parameter+= URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";

		if (isAsync)
		{
			xmlHttpRequest.setOnreadystatechange(new EventHandler()
			{
				public void handleEvent(Event evt)
				{
					if (xmlHttpRequest.getReadyState() == 4 && xmlHttpRequest.getStatus() == 200)
						asyncCallback.onSuccess(xmlHttpRequest.getResponseText());
					else if (xmlHttpRequest.getStatus() == 404)
						asyncCallback.onError();
				}
			});
		}

		xmlHttpRequest.sendSync(parameter);

		return isAsync ? "" : (String) xmlHttpRequest.getResponseText();
	}

	public String executeAsynchronousRequest(String url, Map<String, String> parameters, AsyncCallback<String> asyncCallback)
	{
		AsyncCallback<String> wrappedCallback= wrapCallback(Serializable.class, asyncCallback);
		return executeHttpRequest(true, url, parameters, wrappedCallback, false, useGetMethod);
	}

	public static <T, S> AsyncCallback<S> wrapCallback(final Class<T> type, final AsyncCallback<S> asyncCallback)
	{
		AsyncCallback<S> result;
		if (WebServiceLocator.getInstance().isRemoteDebugging())
		{
			result= new AsyncCallbackWrapper<S>(asyncCallback)
			{
				public void onError()
				{
					asyncCallback.onError();
				}

				public void onSuccess(S result)
				{
					AsyncResponseHandler asyncResponseHandler= createRemoteServiceByWebSocket(AsyncResponseHandler.class);
					String id= (String) ScriptHelper.eval("this.javaId", this);
					asyncResponseHandler.pushResponse((String) result, id);
				}
			};
		}
		else
			result= (AsyncCallback<S>) asyncCallback;

		return result;
	}

	public static <T> T createRemoteServiceByWebSocket(Class<T> type)
	{
		return (T) Proxy.newProxyInstance(RequestExecutorImpl.class.getClassLoader(), new Class[] { type }, new ClientToServerServiceInvocationHandler(type));
	}

	public void executeCrossDomainAsynchronousRequest(String url, Map<String, String> parameters, AsyncCallback<String> asyncCallback)
	{
		AsyncCallback<String> wrappedCallback= wrapCallback(Serializable.class, asyncCallback);
		executeHttpRequest(true, url, parameters, wrappedCallback, true, useGetMethod);
	}

}
