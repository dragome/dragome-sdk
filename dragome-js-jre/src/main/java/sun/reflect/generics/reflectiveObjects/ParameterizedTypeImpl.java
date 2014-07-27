/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl implements ParameterizedType
{
	private Type[] actualTypeArguments;
	private Class<?> rawType;
	private Type ownerType;
	private String genericSignature;

	public ParameterizedTypeImpl(String genericSignature)
	{
		this.genericSignature= genericSignature;
	}

	public Type[] getActualTypeArguments()
	{
		Class<?> type;
		try
		{
			type= Class.forName(genericSignature);
			return new Type[] { type };
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Type getRawType()
	{
		return null;
	}

	public Type getOwnerType()
	{
		return null;
	}
}
