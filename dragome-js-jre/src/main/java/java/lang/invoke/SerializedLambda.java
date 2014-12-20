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
