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
		return super.filterClassPath(classpathEntry) || classpathEntry.contains("dragome-callback-evictor-");
	}
}
