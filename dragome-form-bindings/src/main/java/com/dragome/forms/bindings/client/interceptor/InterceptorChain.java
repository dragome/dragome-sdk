package com.dragome.forms.bindings.client.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.dragome.forms.bindings.extra.user.client.Command;

/**
 * InterceptorChain allows the creation of interceptor chains that can be used to
 * conditionally execute commands in an asynchronous manner.
 */
public class InterceptorChain
{
	private ArrayList<Interceptor> interceptorList= new ArrayList<Interceptor>();

	/**
	 * This adds interceptors to the start of the chain.
	 *
	 * @param interceptor the interceptor to add.
	 */
	public void addInterceptor(Interceptor interceptor)
	{
		interceptorList.add(interceptor);
	}

	/**
	 * Adds interceptors to the start of the chain.
	 * @param interceptors the interceptors to add.
	 */
	public void addInterceptors(Collection<Interceptor> interceptors)
	{
		interceptorList.addAll(interceptors);
	}

	/**
	 * Adds interceptors to the start of the chain.
	 * @param interceptor the first interceptor to add.
	 * @param others any additional interceptors.
	 */
	public void addInterceptors(Interceptor interceptor, Interceptor... others)
	{
		addInterceptor(interceptor);
		addInterceptors(Arrays.asList(others));
	}

	/**
	 * Executes the specified command if and only if all the interceptors proceed.
	 * @param executor the command to execute if all the interceptors proceed.
	 */
	public void execute(Command executor)
	{
		buildInvocationChain(executor).proceed();
	}

	/**
	 * Builds the invocation chain.  This is useful if you need access to the
	 * Invocation although typically you'd call {@link #execute(com.google.gwt.user.client.Command)}
	 * instead.
	 * @param executor the command to run if all interceptors proceed.
	 * @return the invocation chain.
	 */
	public Invocation buildInvocationChain(final Command executor)
	{
		Invocation invocation= new Invocation(new Command()
		{
			public void execute()
			{
				executor.execute();
			}
		});

		for (Interceptor interceptor : interceptorList)
		{
			invocation= new Invocation(invocation, interceptor);
		}

		return invocation;
	}
}
