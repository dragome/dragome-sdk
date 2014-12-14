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
