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
package com.dragome.commons.compiler.annotations;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dragome.commons.compiler.annotations.AnnotationsHelper.AnnotationContainer.AnnotationEntry;

public class AnnotationsHelper
{
	public static class AnnotationContainer
	{
		public static class AnnotationEntry
		{
			private Class<?> type;

			public Class<?> getType()
			{
				return type;
			}

			public String getAnnotationKey()
			{
				return annotationKey;
			}

			public String getAnnotationValue()
			{
				return annotationValue;
			}

			private String annotationKey;
			private String annotationValue;

			public AnnotationEntry(Class<?> type, String annotationKey, String annotationValue)
			{
				this.type= type;
				this.annotationKey= annotationKey;
				this.annotationValue= annotationValue;
			}
		}
		
		private List<AnnotationEntry> entries= new ArrayList<AnnotationsHelper.AnnotationContainer.AnnotationEntry>();

		public List<AnnotationEntry> getEntries()
		{
			return entries;
		}

		public void add(Class<?> type, String annotationKey, String annotationValue)
		{
			entries.add(new AnnotationEntry(type, annotationKey, annotationValue));
		}
	}

	private static Map<Class<? extends Annotation>, AnnotationContainer> annotationsByType= new HashMap<Class<? extends Annotation>, AnnotationContainer>();
	public static List<String> annotations= new ArrayList<String>();

	public static AnnotationContainer getAnnotationsByType(Class<? extends Annotation> aType)
	{
		AnnotationContainer annotationContainer= annotationsByType.get(aType);
		if (annotationContainer == null)
			annotationsByType.put(aType, annotationContainer= new AnnotationContainer());

		return annotationContainer;
	}
	
	public static class AnnotationEntryWithEntityType
	{
		private AnnotationEntry annotationEntry;
		private Class<? extends Annotation> aType;
		
		public AnnotationEntry getAnnotationEntry()
		{
			return annotationEntry;
		}
		
		public Class<? extends Annotation> getAnnotationType()
		{
			return aType;
		}
		
		public AnnotationEntryWithEntityType(final AnnotationEntry annotationEntry, final Class<? extends Annotation> aType)
		{
			this.annotationEntry = annotationEntry;
			this.aType = aType;
		}
	}
	
	public static List<AnnotationEntryWithEntityType> getAnnotationsByClass(final Class<?> aClass)
	{
		final List<AnnotationEntryWithEntityType> ret = new ArrayList<AnnotationEntryWithEntityType>();
		for(final Map.Entry<Class<? extends Annotation>, AnnotationContainer> entrys : annotationsByType.entrySet())
		{
			for (final AnnotationEntry annotationEntry : entrys.getValue().entries)
			{
				if (annotationEntry.getType().equals(aClass))
				{
					ret.add(new AnnotationEntryWithEntityType(annotationEntry, entrys.getKey()));
				}
			}
		}
		return ret;
	}
}
