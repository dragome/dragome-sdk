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
package java.lang.reflect;

import com.dragome.commons.javascript.ScriptHelper;

public final class Array
{
	public static Object newInstance(Class<?> componentType, int length)
	{
		ScriptHelper.put("componentType", componentType, null);
		ScriptHelper.put("length", length, null);
		String realName = ScriptHelper.evalCasting("componentType.realName", String.class, null);
		realName = "[L" + realName + ";";
		ScriptHelper.put("classSignature", realName, null);
		Object eval = ScriptHelper.eval("dragomeJs.newArray(classSignature, [length]);", null);
		return eval;
	}

	public static void set(Object array, int i, Object v)
	{
		ScriptHelper.put("anArray", array, null);
		ScriptHelper.put("i", i, null);
		ScriptHelper.put("aValue", v, null);
		ScriptHelper.eval("anArray[i]=v;", null);
		//TODO revisar
	}

	public static int getLength(Object array)
	{
		ScriptHelper.put("anArray", array, null);
		return ScriptHelper.evalInt("anArray.length", null);
	}

	public static Object get(Object array, int index) throws IllegalArgumentException, ArrayIndexOutOfBoundsException
	{
		ScriptHelper.put("anArray", array, null);
		ScriptHelper.put("i", index, null);
		return ScriptHelper.eval("anArray[i]", null);
	}
}
