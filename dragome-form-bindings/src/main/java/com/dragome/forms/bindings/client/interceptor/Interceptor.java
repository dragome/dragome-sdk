package com.dragome.forms.bindings.client.interceptor;

/**
 * Interceptors can be added to an {@link com.pietschy.gwt.pectin.client.interceptor.InterceptorChain}
 * to conditionally execute a command.
 * <p>
 * The interceptor must either explicitly invoke {@link Invocation#proceed()} or invoke the command
 * returned by {@link Invocation#getProceedCommand()}.  Not invoking one of the above will abort the
 * interceptor chain.
 */
public interface Interceptor
{
	void intercept(Invocation invocation);
}
