/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.services;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Map.Entry;

import com.dragome.commons.ExecutionHandler;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.debugging.messages.ClientToServerServiceInvocationHandler;
import com.dragome.services.interfaces.AsyncCallback;
import com.dragome.services.interfaces.AsyncResponseHandler;

public class RequestExecutorImpl implements RequestExecutor
{
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
			executeHttpRequest(true, url, parameters, wrappedCallback, false);

			executionHandler.suspendExecution();
			return resultArray[0];
		}
		else
			return executeHttpRequest(false, url, parameters, null, false);

	}

	public String executeFixedSynchronousRequest(String url, Map<String, String> parameters)
	{
		return executeHttpRequest(false, url, parameters, null, false);
	}

	public static String executeHttpRequest(boolean isAsync, String url, Map<String, String> parameters, AsyncCallback<String> asyncCallback, boolean crossDomain)
	{
		ScriptHelper.put("url", url, null);
		String isAsyncAsString= isAsync ? "true" : "false";
		String isCrossDomain= crossDomain ? "true" : "false";

		ScriptHelper.put("asyncCall", isAsyncAsString, null);
		ScriptHelper.put("asyncCallback", asyncCallback, null);
		ScriptHelper.put("crossDomain", isCrossDomain, null);

		ScriptHelper.put("parameters2", new Object(), null);
		ScriptHelper.eval("parameters2={}", null);
		if (parameters != null)
			for (Entry<String, String> entry : parameters.entrySet())
			{
				ScriptHelper.put("key", entry.getKey(), null);
				ScriptHelper.put("value", entry.getValue(), null);
				ScriptHelper.eval("parameters2[key]=value", null);
			}

		Object eval= ScriptHelper.eval("jQueryHttpRequest(asyncCall, url,parameters2,asyncCallback, crossDomain)", null);
		return isAsync ? "" : (String) eval;
	}

	public String executeAsynchronousRequest(String url, Map<String, String> parameters, AsyncCallback<String> asyncCallback)
	{
		AsyncCallback<String> wrappedCallback= wrapCallback(Serializable.class, asyncCallback);
		return executeHttpRequest(true, url, parameters, wrappedCallback, false);
	}

	public static <T, S> AsyncCallback<S> wrapCallback(final Class<T> type, final AsyncCallback<S> asyncCallback)
	{
		AsyncCallback<S> result;
		if (ServiceLocator.getInstance().isRemoteDebugging())
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
		executeHttpRequest(true, url, parameters, wrappedCallback, true);
	}

}
