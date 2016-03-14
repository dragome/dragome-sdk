/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Information about method parameters.
 *
 * A {@code Parameter} provides information about method parameters,
 * including its name and modifiers.  It also provides an alternate
 * means of obtaining attributes for the parameter.
 *
 * @since 1.8
 */
public final class Parameter implements AnnotatedElement
{

	private final String name;
	private final int modifiers;
	private final int index;
	private Method method;

	/**
	 * Package-private constructor for {@code Parameter}.
	 *
	 * If method parameter data is present in the classfile, then the
	 * JVM creates {@code Parameter} objects directly.  If it is
	 * absent, however, then {@code Executable} uses this constructor
	 * to synthesize them.
	 *
	 * @param name The name of the parameter.
	 * @param modifiers The modifier flags for the parameter.
	 * @param executable The executable which defines this parameter.
	 * @param index The index of the parameter.
	 */
	public Parameter(Class<?> type, Method method, String name, int modifiers, int index)
	{
		this.type= type;
		this.method= method;
		this.name= name;
		this.modifiers= modifiers;
		this.index= index;
	}

	/**
	 * Get the modifier flags for this the parameter represented by
	 * this {@code Parameter} object.
	 *
	 * @return The modifier flags for this parameter.
	 */
	public int getModifiers()
	{
		return modifiers;
	}

	/**
	 * Returns the name of the parameter.  If the parameter's name is
	 * {@linkplain #isNamePresent() present}, then this method returns
	 * the name provided by the class file. Otherwise, this method
	 * synthesizes a name of the form argN, where N is the index of
	 * the parameter in the descriptor of the method which declares
	 * the parameter.
	 *
	 * @return The name of the parameter, either provided by the class
	 *         file or synthesized if the class file does not provide
	 *         a name.
	 */
	public String getName()
	{
		// Note: empty strings as paramete names are now outlawed.
		// The .equals("") is for compatibility with current JVM
		// behavior.  It may be removed at some point.
		if (name == null || name.equals(""))
			return "arg" + index;
		else
			return name;
	}

	// Package-private accessor to the real name field.
	String getRealName()
	{
		return name;
	}

	/**
	 * Returns a {@code Type} object that identifies the parameterized
	 * type for the parameter represented by this {@code Parameter}
	 * object.
	 *
	 * @return a {@code Type} object identifying the parameterized
	 * type of the parameter represented by this object
	 */
	public Type getParameterizedType()
	{
		Type tmp= parameterTypeCache;
		return tmp;
	}

	private transient volatile Type parameterTypeCache= null;
	private Class<?> type;
	private String methodName;

	/**
	 * Returns a {@code Class} object that identifies the
	 * declared type for the parameter represented by this
	 * {@code Parameter} object.
	 *
	 * @return a {@code Class} object identifying the declared
	 * type of the parameter represented by this object
	 */
	public Class<?> getType()
	{
		Class<?> tmp= type;
		return tmp;
	}

	/**
	 * Returns an AnnotatedType object that represents the use of a type to
	 * specify the type of the formal parameter represented by this Parameter.
	 *
	 * @return an {@code AnnotatedType} object representing the use of a type
	 *         to specify the type of the formal parameter represented by this
	 *         Parameter
	 */
	public AnnotatedType getAnnotatedType()
	{
		return null;
	}

	/**
	 * Returns {@code true} if this parameter is implicitly declared
	 * in source code; returns {@code false} otherwise.
	 *
	 * @return true if and only if this parameter is implicitly
	 * declared as defined by <cite>The Java&trade; Language
	 * Specification</cite>.
	 */
	public boolean isImplicit()
	{
		return false;
	}

	/**
	 * Returns {@code true} if this parameter is neither implicitly
	 * nor explicitly declared in source code; returns {@code false}
	 * otherwise.
	 *
	 * @jls 13.1 The Form of a Binary
	 * @return true if and only if this parameter is a synthetic
	 * construct as defined by
	 * <cite>The Java&trade; Language Specification</cite>.
	 */
	public boolean isSynthetic()
	{
		return false;
	}

	/**
	 * Returns {@code true} if this parameter represents a variable
	 * argument list; returns {@code false} otherwise.
	 *
	 * @return {@code true} if an only if this parameter represents a
	 * variable argument list.
	 */
	public boolean isVarArgs()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 * @throws NullPointerException {@inheritDoc}
	 */
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
	{
		return Class.getAnnotationInternal(type, annotationClass, method.getName(), index);
	}

	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass)
	{
		Objects.requireNonNull(annotationClass);

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Annotation[] getDeclaredAnnotations()
	{
		return null;
	}

	/**
	 * @throws NullPointerException {@inheritDoc}
	 */
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass)
	{
		// Only annotations on classes are inherited, for all other
		// objects getDeclaredAnnotation is the same as
		// getAnnotation.
		return getAnnotation(annotationClass);
	}

	public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass)
	{
		// Only annotations on classes are inherited, for all other
		// objects getDeclaredAnnotations is the same as
		// getAnnotations.
		return getAnnotationsByType(annotationClass);
	}

	/**
	 * {@inheritDoc}
	 */
	public Annotation[] getAnnotations()
	{
		return getDeclaredAnnotations();
	}

	private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;

	private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations()
	{
		if (null == declaredAnnotations)
		{
			declaredAnnotations= new HashMap<Class<? extends Annotation>, Annotation>();
			Annotation[] ann= getDeclaredAnnotations();
			for (int i= 0; i < ann.length; i++)
				declaredAnnotations.put(ann[i].annotationType(), ann[i]);
		}
		return declaredAnnotations;
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationType)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
