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

import java.util.List;

import com.dragome.annotations.ServiceImplementation;
import com.dragome.helpers.serverside.RequestUrlActivityMapperImpl;
import com.dragome.view.VisualActivity;

@ServiceImplementation(RequestUrlActivityMapperImpl.class)
public interface RequestUrlActivityMapper
{
	String getActivityClassNameFromUrl(String uri);
	List<Class<? extends VisualActivity>> getExistingVisualActivities();
	String getActivityAlias(Class<? extends VisualActivity> visualActivity);
}
