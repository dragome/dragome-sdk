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

import java.lang.reflect.Method;

public class MethodHandles
{
	public static final class Lookup
	{
		public static final Object PRIVATE= null;

		public static final Object PUBLIC= null;
		
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
