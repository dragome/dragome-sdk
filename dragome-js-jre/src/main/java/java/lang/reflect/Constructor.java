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
/* Copyright 1998, 2005 The Apache Software Foundation or its licensors, as applicable
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang.reflect;

import java.lang.annotation.Annotation;

import junit.framework.Member;

/**
 * This class must be implemented by the VM vendor. This class models a
 * constructor. Information about the constructor can be accessed, and the
 * constructor can be invoked dynamically.
 *
 * @j2sRequireImport java.lang.Void
 */
public final class Constructor<T> extends Executable
{
	private Class<T> clazz2;
	private Class[] parameterTypes;
	private Class[] exceptionTypes;
	private int modifiers;

	/**
	 * Package-private constructor used by ReflectAccess to enable
	 * instantiation of these objects in Java code from the java.lang
	 * package via sun.reflect.LangReflectAccess.
	 */
	public Constructor(Class<T> declaringClass, Class[] parameterTypes, Class[] checkedExceptions, int modifiers)
	{
		this.clazz2= declaringClass;
		this.parameterTypes= parameterTypes;
		this.exceptionTypes= checkedExceptions;
		this.modifiers= modifiers;
	}

	public TypeVariable<Constructor<T>>[] getTypeParameters()
	{
		return (TypeVariable<Constructor<T>>[]) new TypeVariable<?>[0];
	}

	/**
	 * <p>
	 * Returns the String representation of the constructor's declaration,
	 * including the type parameters.
	 * </p>
	 *
	 * @return An instance of String.
	 * @since 1.5
	 */
	public String toGenericString()
	{
		return null;
	}

	/**
	 * <p>
	 * Gets the parameter types as an array of {@link Type} instances, in
	 * declaration order. If the constructor has no parameters, then an empty
	 * array is returned.
	 * </p>
	 *
	 * @return An array of {@link Type} instances.
	 * @throws GenericSignatureFormatError if the generic method signature is
	 *         invalid.
	 * @throws TypeNotPresentException if the component type points to a missing
	 *         type.
	 * @throws MalformedParameterizedTypeException if the component type points
	 *         to a type that can't be instantiated for some reason.
	 * @since 1.5
	 */
	public Type[] getGenericParameterTypes()
	{
		return new Type[0];
	}

	/**
	 * <p>
	 * Gets the exception types as an array of {@link Type} instances. If the
	 * constructor has no declared exceptions, then an empty array is returned.
	 * </p>
	 *
	 * @return An array of {@link Type} instances.
	 * @throws GenericSignatureFormatError if the generic method signature is
	 *         invalid.
	 * @throws TypeNotPresentException if the component type points to a missing
	 *         type.
	 * @throws MalformedParameterizedTypeException if the component type points
	 *         to a type that can't be instantiated for some reason.
	 * @since 1.5
	 */
	public Type[] getGenericExceptionTypes()
	{
		return new Type[0];
	}

	/**
	 * <p>
	 * Gets an array of arrays that represent the annotations of the formal
	 * parameters of this constructor. If there are no parameters on this
	 * constructor, then an empty array is returned. If there are no annotations
	 * set, then and array of empty arrays is returned.
	 * </p>
	 *
	 * @return An array of arrays of {@link Annotation} instances.
	 * @since 1.5
	 */
	public Annotation[][] getParameterAnnotations()
	{
		return new Annotation[0][0];
	}

	/**
	 * <p>
	 * Indicates whether or not this constructor takes a variable number
	 * argument.
	 * </p>
	 *
	 * @return A value of <code>true</code> if a vararg is declare, otherwise
	 *         <code>false</code>.
	 * @since 1.5
	 */
	public boolean isVarArgs()
	{
		return false;
	}

	public boolean isSynthetic()
	{
		return false;
	}

	/**
	* Compares the specified object to this Constructor and answer if they are
	* equal. The object must be an instance of Constructor with the same
	* defining class and parameter types.
	*
	* @param object the object to compare
	* @return true if the specified object is equal to this Constructor, false
	*         otherwise
	* @see #hashCode
	*/
	public boolean equals(Object object)
	{
		if (object != null && object instanceof Constructor)
		{
			Constructor other= (Constructor) object;
			if (getDeclaringClass() == other.getDeclaringClass())
			{
				/* Avoid unnecessary cloning */
				Class[] params1= parameterTypes;
				Class[] params2= other.parameterTypes;
				if (params1.length == params2.length)
				{
					for (int i= 0; i < params1.length; i++)
					{
						if (params1[i] != params2[i])
							return false;
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return the {@link Class} associated with the class that defined this
	 * constructor.
	 *
	 * @return the declaring class
	 */
	public Class<T> getDeclaringClass()
	{
		return clazz2;
	}

	/**
	 * Return an array of the {@link Class} objects associated with the
	 * exceptions declared to be thrown by this constructor. If the constructor
	 * was not declared to throw any exceptions, the array returned will be
	 * empty.
	 *
	 * @return the declared exception classes
	 */
	public Class<?>[] getExceptionTypes()
	{
		return exceptionTypes;
	}

	/**
	 * Return the modifiers for the modelled constructor. The Modifier class
	 * should be used to decode the result.
	 *
	 * @return the modifiers
	 * @see java.lang.reflect.Modifier
	 */
	public int getModifiers()
	{
		return modifiers;
	}

	/**
	 * Return the name of the modelled constructor. This is the name of the
	 * declaring class.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return getDeclaringClass().getName();
	}

	/**
	 * Return an array of the {@link Class} objects associated with the
	 * parameter types of this constructor. If the constructor was declared with
	 * no parameters, the array returned will be empty.
	 *
	 * @return the parameter types
	 */
	public Class<?>[] getParameterTypes()
	{
		return parameterTypes;
	}

	/**
	 * Answers an integer hash code for the receiver. Objects which are equal
	 * answer the same value for this method. The hash code for a Constructor is
	 * the hash code of the declaring class' name.
	 *
	 * @return the receiver's hash
	 * @see #equals
	 */
	public int hashCode()
	{
		return getDeclaringClass().getName().hashCode();
	}

	/**
	 * Return a new instance of the declaring class, initialized by dynamically
	 * invoking the modelled constructor. This reproduces the effect of
	 * <code>new declaringClass(arg1, arg2, ... , argN)</code> This method
	 * performs the following:
	 * <ul>
	 * <li>A new instance of the declaring class is created. If the declaring
	 * class cannot be instantiated (i.e. abstract class, an interface, an array
	 * type, or a base type) then an InstantiationException is thrown.</li>
	 * <li>If this Constructor object is enforcing access control (see
	 * AccessibleObject) and the modelled constructor is not accessible from the
	 * current context, an IllegalAccessException is thrown.</li>
	 * <li>If the number of arguments passed and the number of parameters do
	 * not match, an IllegalArgumentException is thrown.</li>
	 * <li>For each argument passed:
	 * <ul>
	 * <li>If the corresponding parameter type is a base type, the argument is
	 * unwrapped. If the unwrapping fails, an IllegalArgumentException is
	 * thrown.</li>
	 * <li>If the resulting argument cannot be converted to the parameter type
	 * via a widening conversion, an IllegalArgumentException is thrown.</li>
	 * </ul>
	 * <li>The modelled constructor is then invoked. If an exception is thrown
	 * during the invocation, it is caught and wrapped in an
	 * InvocationTargetException. This exception is then thrown. If the
	 * invocation completes normally, the newly initialized object is returned.
	 * </ul>
	 *
	 * @param args
	 *            the arguments to the constructor
	 * @return the new, initialized, object
	 * @exception java.lang.InstantiationException
	 *                if the class cannot be instantiated
	 * @exception java.lang.IllegalAccessException
	 *                if the modelled constructor is not accessible
	 * @exception java.lang.IllegalArgumentException
	 *                if an incorrect number of arguments are passed, or an
	 *                argument could not be converted by a widening conversion
	 * @exception java.lang.reflect.InvocationTargetException
	 *                if an exception was thrown by the invoked constructor
	 * @see java.lang.reflect.AccessibleObject
	*
	* @j2sNative
	* var instance = new this.clazz (Clazz.inheritArgs);
	* Clazz.instantialize (instance, args);
	* return instance;
	 */
	public T newInstance(Object... args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return clazz2.newInstance();
	}

	/**
	 * Answers a string containing a concise, human-readable description of the
	 * receiver. The format of the string is modifiers (if any) declaring class
	 * name '(' parameter types, separated by ',' ')' If the constructor throws
	 * exceptions, ' throws ' exception types, separated by ',' For example:
	 * <code>public String(byte[],String) throws UnsupportedEncodingException</code>
	 *
	 * @return a printable representation for the receiver
	 */
	public String toString()
	{
		return null;
	}
}
