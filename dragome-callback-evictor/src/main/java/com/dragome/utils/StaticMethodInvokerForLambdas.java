package com.dragome.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import com.dragome.callbackevictor.enhancers.StackRecorder;
import com.dragome.commons.ContinueReflection;

public class StaticMethodInvokerForLambdas implements MethodHolder
{
	private Method method;
	private Object[] args;

	public StaticMethodInvokerForLambdas()
	{
	}

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

			Object result= method2.invoke(null, args);

			StackRecorder stackRecorder= StackRecorder.get();
			if (stackRecorder != null && stackRecorder.isCapturing)
				stackRecorder.pushReference(this);

			return result;
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

	public void setMethod(Method method)
	{
		this.method= method;
	}

	public List<Object> getArgs()
	{
		return Arrays.asList(args);
	}

	public void setArgs(List<Object> parameters)
	{
		this.args= parameters.toArray();
	}
}