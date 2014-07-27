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
package com.dragome.helpers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.services.ServiceLocator;

public class AnnotationsHelper
{
	public static class AnnotationContainer
	{
		public class AnnotationEntry
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
	private static List<String> annotations= new ArrayList<String>();

	public static AnnotationContainer getAnnotationsByType(Class<? extends Annotation> aType)
	{
		AnnotationContainer annotationContainer= annotationsByType.get(aType);
		if (annotationContainer == null)
			annotationsByType.put(aType, annotationContainer= new AnnotationContainer());

		return annotationContainer;
	}

	@MethodAlias(alias= "dragomeJs.addTypeAnnotation")
	public static void addTypeAnnotation(String className, String annotationClassName, String annotationKey, String annotationValue)
	{
		try
		{
			Class<?> type= ServiceLocator.getInstance().getReflectionService().forName(className);
			Class<? extends Annotation> annotationClass= (Class<? extends Annotation>) ServiceLocator.getInstance().getReflectionService().forName(annotationClassName);

			AnnotationContainer map= getAnnotationsByType(annotationClass);
			map.add(type, annotationKey, annotationValue);

			annotations.add(className + "|" + annotationClassName + "|" + annotationKey + "|" + annotationValue);
		}
		catch (Exception e)
		{
			Logger.getLogger(AnnotationsHelper.class.getName()).log(Level.FINEST, "Cannot add type annotation", e);
		}
	}
}
