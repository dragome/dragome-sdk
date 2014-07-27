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

import java.lang.reflect.Method;

public class MethodHandles
{
	public static final class Lookup
	{
		public static final Object PRIVATE= null;
		
		private Class<?> requestedLookupClass;

		public Class<?> getRequestedLookupClass()
		{
			return requestedLookupClass;
		}

		public void setRequestedLookupClass(Class<?> requestedLookupClass)
		{
			this.requestedLookupClass= requestedLookupClass;
		}

		public Method getMethod()
		{
			return method;
		}

		public void setMethod(Method method)
		{
			this.method= method;
		}

		public Class<?> getSpecialCaller()
		{
			return specialCaller;
		}

		public void setSpecialCaller(Class<?> specialCaller)
		{
			this.specialCaller= specialCaller;
		}

		private Method method;
		private Class<?> specialCaller;

		public Lookup in(Class<?> requestedLookupClass)
		{
			this.requestedLookupClass= requestedLookupClass;
			return this;
		}

		public MethodHandle unreflectSpecial(Method m, Class<?> specialCaller) throws IllegalAccessException
		{
			this.method= m;
			this.specialCaller= specialCaller;
			return new MethodHandle(this);
		}
	}

	public static Lookup lookup()
	{
		return new Lookup();
	}

}
