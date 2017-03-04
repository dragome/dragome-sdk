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
		try
		{
			String actualTypeArgumentsString= genericSignature.substring(genericSignature.indexOf("<") + 2, genericSignature.lastIndexOf(">") - 1);
			if (actualTypeArgumentsString.contains("<"))
				actualTypeArguments= new Type[] { new ParameterizedTypeImpl(actualTypeArgumentsString) };
			else
				actualTypeArguments= new Type[] { Class.forName(removeSpecialChars(actualTypeArgumentsString)) };

			String rawTypeString= genericSignature.substring(0, genericSignature.indexOf("<"));

			rawType= Class.forName(removeSpecialChars(rawTypeString));
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	private String removeSpecialChars(String signature)
	{
		signature= signature.replaceAll("^L", "");
		signature= signature.replace("/", "_");
		return signature;
	}

	public Type[] getActualTypeArguments()
	{
		return actualTypeArguments;
	}

	public Type getRawType()
	{
		return rawType;
	}

	public Type getOwnerType()
	{
		return null;
	}
}
