package com.dragome.utils;

import java.lang.reflect.Method;

import com.dragome.commons.ContinueReflection;

public class StaticMethodInvokerForLambdas implements MethodHolder
{
	private final Method method;
	Object[] args;

	public StaticMethodInvokerForLambdas(Method method, Object[] args)
	{
		this.method= method;
		this.args= args;
	}

	@ContinueReflection
	public Object invoke() throws Exception
	{
		try
		{
			Method method2= method;
			Object[] args= this.args;

			return method2.invoke(null, args);
		}
		catch (Exception e)
		{
		}
		return null;
	}

	public Method getMethod()
	{
		return method;
	}
}