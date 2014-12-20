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
package com.dragome.helpers.serverside;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import com.dragome.annotations.PageAlias;
import com.dragome.helpers.RequestUrlActivityMapper;
import com.dragome.view.VisualActivity;

public class RequestUrlActivityMapperImpl implements RequestUrlActivityMapper
{
	public String getActivityClassNameFromUrl(String uri)
	{
		Reflections reflections= new Reflections("");
		Set<Class<? extends VisualActivity>> modules= reflections.getSubTypesOf(VisualActivity.class);
		Set<Class<?>> aliases= reflections.getTypesAnnotatedWith(PageAlias.class);

		for (Class<?> type : aliases)
		{
			PageAlias pageAlias= type.getAnnotation(PageAlias.class);
			if (pageAlias != null && uri.contains(pageAlias.alias()))
				return type.getName();
		}

		for (Class<?> type : modules)
		{
			if (uri.contains(type.getSimpleName()))
				return type.getName();
		}

		return null;
	}

	public List<Class<? extends VisualActivity>> getExistingVisualActivities()
	{
		Reflections reflections= new Reflections("");
		Set<Class<? extends VisualActivity>> classes= reflections.getSubTypesOf(VisualActivity.class);
		return new ArrayList<Class<? extends VisualActivity>>(classes);
	}

	public String getActivityAlias(Class<? extends VisualActivity> visualActivity)
	{
		PageAlias pageAlias= visualActivity.getAnnotation(PageAlias.class);
		return pageAlias != null ? pageAlias.alias() : "";
	}
}
