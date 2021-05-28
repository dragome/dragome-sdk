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
package java.lang.invoke;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

import com.dragome.commons.ProxyRelatedInvocationHandler;
import com.dragome.commons.javascript.ScriptHelper;

public class MethodHandle
{
	private Object x;
	private Lookup lookup;
	private Method foundMethod;

	public MethodHandle(Lookup lookup)
	{
		this.lookup= lookup;
	}

	public MethodHandle bindTo(Object x)
	{
		if (x instanceof ProxyRelatedInvocationHandler)
		{
			ProxyRelatedInvocationHandler proxyRelatedInvocationHandler= (ProxyRelatedInvocationHandler) x;
			x= proxyRelatedInvocationHandler.getProxy();
		}
		this.x= x;
		findMethod();
		return this;
	}

	public Object invokeWithArguments(Object... args) throws Throwable
	{
		ScriptHelper.put("type", foundMethod.getDeclaringClass(), this);
		ScriptHelper.put("args", args, this);
		ScriptHelper.put("proxy", x, this);
		ScriptHelper.put("method", lookup.getMethod(), this);

		boolean notMembers= ScriptHelper.evalBoolean("type.$$$nativeClass___java_lang_Object.$$members === undefined", this);
		Object o;
		if (notMembers)
			o= ScriptHelper.eval("type.$$$nativeClass___java_lang_Object.prototype[method.$$$signature___java_lang_String].apply(proxy, args)", this);
		else
			o= ScriptHelper.eval("type.$$$nativeClass___java_lang_Object.$$members[method.$$$signature___java_lang_String].apply(proxy, args)", this);
		return o;
	}

	public void findMethod()
	{
		Method method= lookup.getMethod();
		Class<?> specialCaller= lookup.getSpecialCaller();
		List<Method> internalGetMethods= specialCaller.internalGetMethods(false);

		for (Method aMethod : internalGetMethods)
		{
			if (aMethod.getName().equals(method.getName()))
			{
				if (aMethod.getReturnType().equals(method.getReturnType()))
				{
					List<TypeVariable<?>> typeParameters1= Arrays.asList(method.getTypeParameters());
					List<TypeVariable<?>> typeParameters2= Arrays.asList(aMethod.getTypeParameters());
					if (typeParameters1.equals(typeParameters2))
					{
						foundMethod= aMethod;
					}
				}
			}
		}
	}
}
