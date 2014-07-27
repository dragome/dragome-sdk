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

public class LambdaConversionException extends Exception
{
	public LambdaConversionException()
	{
	}

	public LambdaConversionException(String message)
	{
		super(message);
	}

	public LambdaConversionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public LambdaConversionException(Throwable cause)
	{
		super(cause);
	}
}
