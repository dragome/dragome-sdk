/*
 * Copyright 2009 Andrew Pietsch 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 *      
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions 
 * and limitations under the License. 
 */

package com.dragome.forms.bindings.client.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Aug 6, 2009
 * Time: 5:19:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectionConverters
{
	public static final CollectionConverter<Collection> COLLECTION_CONVERTER= new CollectionConverter<Collection>()
	{
		public Collection<?> fromBean(Collection source)
		{
			return source;
		}

		public Collection toBean(List<?> source)
		{
			return new ArrayList<Object>(source);
		}
	};

	public static final CollectionConverter<List> LIST_CONVERTER= new CollectionConverter<List>()
	{
		public Collection<?> fromBean(List source)
		{
			return source;
		}

		public List toBean(List<?> source)
		{
			return new ArrayList<Object>(source);
		}
	};

	public static final CollectionConverter<Set> SET_CONVERTER= new CollectionConverter<Set>()
	{
		public Collection<?> fromBean(Set source)
		{
			return source;
		}

		public Set toBean(List<?> source)
		{
			return new HashSet<Object>(source);
		}
	};

	public static final CollectionConverter<SortedSet> SORTED_SET_CONVERTER= new CollectionConverter<SortedSet>()
	{
		public Collection<?> fromBean(SortedSet source)
		{
			return source;
		}

		public SortedSet toBean(List<?> source)
		{
			return new TreeSet<Object>(source);
		}
	};

	private HashMap<Class<?>, CollectionConverter<?>> converters= new HashMap<Class<?>, CollectionConverter<?>>();

	public CollectionConverters()
	{
		register(List.class, LIST_CONVERTER);
		register(Set.class, SET_CONVERTER);
		register(SortedSet.class, SORTED_SET_CONVERTER);
		register(Collection.class, COLLECTION_CONVERTER);
	}

	public <T> void register(Class<T> collectionClass, CollectionConverter<T> converter)
	{
		converters.put(collectionClass, converter);
	}

	@SuppressWarnings("unchecked")
	public <T> CollectionConverter<T> getConverter(Class<T> aClass)
	{
		return (CollectionConverter<T>) converters.get(aClass);
	}
}
