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
package com.dragome.callbackevictor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.Executor;

import com.dragome.callbackevictor.enhancers.Continuation;
import com.dragome.callbackevictor.serverside.ContinuationBytecodeTransformer;
import com.dragome.commons.ExecutionHandler;
import com.dragome.commons.InstrumentationDragomeConfigurator;
import com.dragome.commons.compiler.BytecodeTransformer;

public class CallbackEvictorConfigurator extends InstrumentationDragomeConfigurator
{
	public CallbackEvictorConfigurator()
	{
		includedPaths.addAll(Arrays.asList(/*"java.lang.reflect.Proxy", "java.lang.reflect.Method",*/"com.dragome.utils.DragomeCallsiteFactory$InvocationHandlerForLambdas", "com.dragome.serverside.servlets.ServiceInvoker", "com.dragome.model.EventDispatcherImpl", "com.dragome.model.EventExecutor", "com.dragome.services", "com.dragome.helpers.DiscovererPage", "com.dragome.services.RemoteServicesHelper", "com.dragome.services.RequestExecutor", "com.dragome.render.html", "com.dragome.debugging.execution", "com.dragome.callbackevictor.CallbackEvictorConfigurator", "com.dragome.examples"));
		loadedFromParent.addAll(new HashSet<String>(Arrays.asList("org.atmosphere", "com.dragome.commons.ProxyRelatedInvocationHandler", "java.", "javax.", "net.sf.saxon")));
	}

	public BytecodeTransformer getBytecodeTransformer()
	{
		return new ContinuationBytecodeTransformer(includedPaths, enabled);
	}

	public ExecutionHandler getExecutionHandler()
	{
		if (!enabled)
			return super.getExecutionHandler();
		else
			return new ExecutionHandler()
			{
				public void suspendExecution()
				{
					Continuation.suspend();
				}

				public void continueExecution()
				{
					Continuation.continueWith(Continuation.current);
				}

				public Executor getExecutor()
				{
					return new Executor()
					{
						public void execute(Runnable command)
						{
							Continuation.startWith(command);
						}
					};
				}

				public boolean canSuspend()
				{
					return enabled;
				}
			};
	}

	public boolean filterClassPath(String classpathEntry)
	{
		return super.filterClassPath(classpathEntry) || classpathEntry.contains("dragome-callback-evictor-" + dragomeVersion + ".jar");
	}
}
