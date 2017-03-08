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
import java.util.List;

import com.dragome.commons.javascript.ScriptHelper;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

public final class Field extends AccessibleObject implements Member
{
	private String signature;
	private Class<?> fieldType;
	private Class<?> class1;
	private int modifier;

	public Field(Class<?> class1, String signature, int modifier)
	{
		this.class1= class1;
		this.signature= signature;
		this.modifier= modifier;

		try
		{
			String type= signature.substring(signature.indexOf("___") + 3);
			this.fieldType= Class.forName(type);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public String getName()
	{
		String name= signature.substring(3);

		int parametersStart= name.indexOf("___");
		if (parametersStart != -1)
			return name.substring(0, parametersStart);
		else
			return name;
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
	{
		return Class.getAnnotationInternal(class1, annotationClass, null, null, getName());
	}

	public Object get(Object obj) throws IllegalAccessException, IllegalArgumentException
	{
		ScriptHelper.put("obj", obj, this);
		ScriptHelper.put("sig", this.signature, this);
		Object result;

		if ((getModifiers() & Modifier.STATIC) != 0)
			result= ScriptHelper.eval("obj.constructor[sig]", this);
		else
			result= ScriptHelper.eval("obj[sig]", this);
		
		return Method.adaptResult(result, getType());
	}

	Object[] tmpArray= new Object[1];

	public void set(Object obj, Object value) throws IllegalAccessException, IllegalArgumentException
	{
		tmpArray[0]= value;
		Method.boxArguments(new Class<?>[] { fieldType }, tmpArray); // #FIXME  passing the value will cause a bug. the value wont be updated because 2nd compiler changed its var name. Static also dont change.

		ScriptHelper.put("obj", obj, this);
		ScriptHelper.put("sig", this.signature, this);
		ScriptHelper.put("value", tmpArray[0], this);

		if ((getModifiers() & Modifier.STATIC) != 0)
			ScriptHelper.evalNoResult("obj.constructor[sig]= value", this);
		else
			ScriptHelper.evalNoResult("obj[sig]= value", this);
	}

	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass)
	{
		return null;
	}

	public Annotation[] getDeclaredAnnotations()
	{
		final List<Annotation> annotations= Class.getAnnotationsInternal(class1, null, null, getName());
		final Annotation[] ret= new Annotation[annotations.size()];
		annotations.toArray(ret);
		return ret;
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
		Class<?> declaringClass= getDeclaringClass();
		ScriptHelper.put("declaringClass", declaringClass, this);
		if (ScriptHelper.evalBoolean("declaringClass.$$$nativeClass___java_lang_Object.$$$$signatures ", this))
		{
			String genericSignature= (String) ScriptHelper.eval("declaringClass.$$$nativeClass___java_lang_Object.$$$$signatures[this.$$$signature___java_lang_String]", this);
			return new ParameterizedTypeImpl(genericSignature);
		}
		else
			return getType();
	}

	public boolean equals(Object object)
	{
		return false;
	}

	public boolean getBoolean(Object object) throws IllegalAccessException, IllegalArgumentException
	{
		return (boolean) this.get(object);
	}

	public byte getByte(Object object) throws IllegalAccessException, IllegalArgumentException
	{
		return (byte) this.get(object);
	}

	public char getChar(Object object) throws IllegalAccessException, IllegalArgumentException
	{
		return (char) this.get(object);
	}

	public Class<?> getDeclaringClass()
	{
		return class1;
	}

	public double getDouble(Object object) throws IllegalAccessException, IllegalArgumentException
	{
		return (double) this.get(object);
	}

	public float getFloat(Object object) throws IllegalAccessException, IllegalArgumentException
	{
		return (float) this.get(object);
	}

	public int getInt(Object object) throws IllegalAccessException, IllegalArgumentException
	{
		return (int) this.get(object);
	}

	public long getLong(Object object) throws IllegalAccessException, IllegalArgumentException
	{
		return (long) this.get(object);
	}

	public short getShort(Object object) throws IllegalAccessException, IllegalArgumentException
	{
		return (short) this.get(object);
	}

	public int getModifiers()
	{
		return modifier;
	}

	native String getSignature();

	public Class<?> getType()
	{
		return fieldType;
	}

	public int hashCode()
	{
		return 0;
	}

	public void setBoolean(Object object, boolean value) throws IllegalAccessException, IllegalArgumentException
	{
		this.set(object, value);
	}

	public void setByte(Object object, byte value) throws IllegalAccessException, IllegalArgumentException
	{
		this.set(object, value);
	}

	public void setChar(Object object, char value) throws IllegalAccessException, IllegalArgumentException
	{
		this.set(object, value);
	}

	public void setDouble(Object object, double value) throws IllegalAccessException, IllegalArgumentException
	{
		this.set(object, value);
	}

	public void setFloat(Object object, float value) throws IllegalAccessException, IllegalArgumentException
	{
		this.set(object, value);
	}

	public void setInt(Object object, int value) throws IllegalAccessException, IllegalArgumentException
	{
		this.set(object, value);
	}

	public void setLong(Object object, long value) throws IllegalAccessException, IllegalArgumentException
	{
		this.set(object, value);
	}

	public void setShort(Object object, short value) throws IllegalAccessException, IllegalArgumentException
	{
		this.set(object, value);
	}

	public String toString()
	{
		return null;
	}
}
