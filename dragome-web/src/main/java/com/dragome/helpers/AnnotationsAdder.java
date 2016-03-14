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

import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dragome.commons.compiler.annotations.AnnotationsHelper;
import com.dragome.commons.compiler.annotations.AnnotationsHelper.AnnotationContainer;
import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;

public class AnnotationsAdder
{
	@MethodAlias(alias= "dragomeJs.addTypeAnnotation")
	public static void addTypeAnnotation(String className, String annotationClassName, String annotationKey, String annotationValue)
	{
		try
		{
			WebServiceLocator.getInstance().setClientSideEnabled(true);
			Class<?> type= ServiceLocator.getInstance().getReflectionService().forName(className);
			Class<? extends Annotation> annotationClass= (Class<? extends Annotation>) ServiceLocator.getInstance().getReflectionService().forName(annotationClassName);

			AnnotationContainer map= AnnotationsHelper.getAnnotationsByType(annotationClass);
			map.add(type, annotationKey, annotationValue);

			AnnotationsHelper.annotations.add(className + "|" + annotationClassName + "|" + annotationKey + "|" + annotationValue);
		}
		catch (Exception e)
		{
			Logger.getLogger(AnnotationsAdder.class.getName()).log(Level.FINEST, "Cannot add type annotation", e);
		}
	}
}
