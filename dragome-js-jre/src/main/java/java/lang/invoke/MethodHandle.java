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

import com.dragome.commons.javascript.ScriptHelper;

public class MethodHandle
{
	private Object x;
	private Lookup lookup;

	public MethodHandle(Lookup lookup)
	{
		this.lookup= lookup;
	}

	public MethodHandle bindTo(Object x)
	{
		this.x= x;
		return this;
	}

	public Object invokeWithArguments(Object... args) throws Throwable
	{
		ScriptHelper.put("type", lookup.getSpecialCaller(), this);
		ScriptHelper.put("args", args, this);
		ScriptHelper.put("proxy", x, this);
		ScriptHelper.put("method", lookup.getMethod(), this);
		Object o= ScriptHelper.eval("type.$$$nativeClass.$$members[method.$$$signature].apply(proxy, args)", this);
		return o;
	}
}
