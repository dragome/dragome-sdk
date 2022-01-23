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
package com.dragome.helpers;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class DragomeEntityManager
{
	protected static Map<String, Object> entities= new Hashtable<>();
	protected static Map<String, String> aliases= new Hashtable<>();
	protected static Map<String, String> inverseAliases= new Hashtable<>();

	public static String add(Object entity)
	{
		String identityHashCode= getEntityId(entity);

		String string= inverseAliases.get(identityHashCode);

		if (string != null)
			return string;

		entities.put(identityHashCode, entity);
		return identityHashCode;
	}

	public static Object remove(Object entity)
	{
		String identityHashCode= getEntityId(entity);
		return entities.remove(identityHashCode);
	}

	public static Object get(String id)
	{
		if (id == null)
			return entities.get(aliases.get("global"));

		String entityId= aliases.get(id);

		if (entityId != null)
			id= entityId;

		return entities.get(id);
	}

	public static String getEntityId(Object object)
	{
		return System.identityHashCode(object) + "";
	}

	public static void clear()
	{
		entities.clear();
		aliases.clear();
		inverseAliases.clear();
	}

	public static void put(String id, Object object)
	{
		if (id == null)
			id= "global";

		String objectId= getEntityId(object);

		if (id != null && !id.equals(objectId))
		{
			aliases.put(id, objectId);
			inverseAliases.put(objectId, id);
		}

		entities.put(objectId, object);
	}

	public static String getId(Object object)
	{
		String objectId= getEntityId(object);

		String result= aliases.get(objectId);

		if (result == null)
			result= getEntityId(object);

		return result;
	}
}
