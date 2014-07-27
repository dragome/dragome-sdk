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
package java.lang.invoke;

import java.io.Serializable;

public final class SerializedLambda implements Serializable
{
	public String getCapturingClass()
	{
		return "";
	}

	public String getFunctionalInterfaceClass()
	{
		return "";
	}

	public String getFunctionalInterfaceMethodName()
	{
		return "";
	}

	public String getFunctionalInterfaceMethodSignature()
	{
		return "";
	}

	public String getImplClass()
	{
		return "";
	}

	public String getImplMethodName()
	{
		return "";
	}

	public String getImplMethodSignature()
	{
		return "";
	}

	public int getImplMethodKind()
	{
		return 0;
	}

	public final String getInstantiatedMethodType()
	{
		return "";
	}

	public int getCapturedArgCount()
	{
		return 0;
	}

	public Object getCapturedArg(int i)
	{
		return null;
	}
}
