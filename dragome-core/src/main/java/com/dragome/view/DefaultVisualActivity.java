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
package com.dragome.view;

import com.dragome.services.ServiceLocator;
import com.dragome.services.interfaces.ParametersHandler;
import com.dragome.services.interfaces.ServiceFactory;

public abstract class DefaultVisualActivity implements VisualActivity
{
	protected ServiceFactory serviceFactory= ServiceLocator.getInstance().getServiceFactory();
	protected ParametersHandler parametersHandler= ServiceLocator.getInstance().getParametersHandler();

	public DefaultVisualActivity()
	{
	}

	public void onDestroy()
	{
	}

	public void onPause()
	{
	}

	public void onRestart()
	{
	}

	public void onResume()
	{
	}

	public void onStart()
	{
	}

	public void onStop()
	{
	}

	public void openActivity(VisualActivity visualActivity)
	{
		visualActivity.onCreate();
	}
	
	public void onCreate()
	{
		build();
	}
}