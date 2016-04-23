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

import java.lang.annotation.Annotation;

import com.dragome.commons.javascript.ScriptHelper;

public final class Field extends AccessibleObject implements Member
{
	private String signature;
	private Class<?> class1;
	private int modifier;

	public Field(Class<?> class1, String signature, int modifier)
	{
		this.class1= class1;
		this.signature= signature;
		this.modifier= modifier;
	}

	public String getName()
	{
		return signature.replace("$", "");
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
	{
		return Class.getAnnotationInternal(class1, annotationClass, null, null, getName());
	}

	public Object get(Object obj) throws IllegalAccessException, IllegalArgumentException
	{
		ScriptHelper.put("obj", obj, this);
		ScriptHelper.put("sig", this.signature, this);

		Object result= ScriptHelper.eval("obj[sig]", this);
		return Method.adaptResult(result, result.getClass());
	}

	public void set(Object obj, Object value) throws IllegalAccessException, IllegalArgumentException
	{
		Method.boxArguments(new Class<?>[] { value.getClass() }, value);

		ScriptHelper.put("obj", obj, this);
		ScriptHelper.put("sig", this.signature, this);
		ScriptHelper.put("value", value, this);

		ScriptHelper.evalNoResult("obj[sig]= value", this);
	}

	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass)
	{
		return null;
	}

	public Annotation[] getDeclaredAnnotations()
	{
		return null;
	}

	public boolean isSynthetic()
	{
		return false;
	}

	public String toGenericString()
	{
		return null;
	}

	public boolean isEnumConstant()
	{
		return false;
	}

	public Type getGenericType()
	{
		try
		{
			return Class.forName("com.dragome.example.MessageService");
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public boolean equals(Object object)
	{
		return false;
	}

	public native boolean getBoolean(Object object) throws IllegalAccessException, IllegalArgumentException;

	public native byte getByte(Object object) throws IllegalAccessException, IllegalArgumentException;

	public native char getChar(Object object) throws IllegalAccessException, IllegalArgumentException;

	public Class<?> getDeclaringClass()
	{
		return class1;
	}

	public native double getDouble(Object object) throws IllegalAccessException, IllegalArgumentException;

	public native float getFloat(Object object) throws IllegalAccessException, IllegalArgumentException;

	public native int getInt(Object object) throws IllegalAccessException, IllegalArgumentException;

	public native long getLong(Object object) throws IllegalAccessException, IllegalArgumentException;

	public int getModifiers()
	{
		return 0;
	}

	public native short getShort(Object object) throws IllegalAccessException, IllegalArgumentException;

	native String getSignature();

	public Class<?> getType()
	{
		return null;
	}

	public int hashCode()
	{
		return 0;
	}

	public native void setBoolean(Object object, boolean value) throws IllegalAccessException, IllegalArgumentException;

	public native void setByte(Object object, byte value) throws IllegalAccessException, IllegalArgumentException;

	public native void setChar(Object object, char value) throws IllegalAccessException, IllegalArgumentException;

	public native void setDouble(Object object, double value) throws IllegalAccessException, IllegalArgumentException;

	public native void setFloat(Object object, float value) throws IllegalAccessException, IllegalArgumentException;

	public native void setInt(Object object, int value) throws IllegalAccessException, IllegalArgumentException;

	public native void setLong(Object object, long value) throws IllegalAccessException, IllegalArgumentException;

	public native void setShort(Object object, short value) throws IllegalAccessException, IllegalArgumentException;

	public String toString()
	{
		return null;
	}
}
