/**
 * Copyright 2007 Charlie Hubbard and Brandon Goodin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package flexjson.transformer;

import java.util.Arrays;
import java.util.HashMap;

/**
 * This class is used to lookup type transformers from specific to generic implementation.
 * For example if an ArrayList transformer is provided
 */
public class TypeTransformerMap extends HashMap<Class, Transformer>
{

	private TypeTransformerMap parentTransformerMap;

	public TypeTransformerMap()
	{
	}

	public TypeTransformerMap(TypeTransformerMap parentTransformerMap)
	{
		this.parentTransformerMap= parentTransformerMap;
	}

	@SuppressWarnings("unchecked")
	public Transformer getTransformer(Object key)
	{
		Transformer transformer= findTransformer(key == null ? void.class : key.getClass(), key == null ? void.class : key.getClass());
		if (transformer == null && parentTransformerMap != null)
		{
			// if no transformers found in child then check parent
			transformer= parentTransformerMap.getTransformer(key);
			if (transformer != null)
			{
				updateTransformers(key == null ? void.class : key.getClass(), transformer);
			}
		}
		return transformer;
	}

	Transformer findTransformer(Class key, Class originalKey)
	{

		if (key == null)
			return null;

		// if specific type found
		if (containsKey(key))
		{
			if (key != originalKey)
			{
				return updateTransformers(originalKey, get(key));
			}
			else
			{
				return get(key);
			}
		}

		// handle arrays specially if no specific array type handler
		// Arrays.class is used for this because it would never appear
		// in an object that needs to be serialized.
		if (key.isArray())
		{
			return updateTransformers(originalKey, get(Arrays.class));
		}

		// check for interface transformer
		for (Class interfaze : key.getInterfaces())
		{

			if (containsKey(interfaze))
			{
				return updateTransformers(originalKey, get(interfaze));
			}
			else
			{
				Transformer t= findTransformer(interfaze, originalKey);
				if (t != null)
					return t;
			}
		}

		// if no interface transformers then check superclass
		return findTransformer(key.getSuperclass(), originalKey);

	}

	private Transformer updateTransformers(Class key, Transformer transformer)
	{
		// only make changes to the child TypeTransformerMap
		if (transformer != null)
		{
			put(key, transformer);
		}
		return transformer;
	}
}
