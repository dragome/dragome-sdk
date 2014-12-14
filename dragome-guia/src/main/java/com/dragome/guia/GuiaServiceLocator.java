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
package com.dragome.guia;

import com.dragome.render.html.HTMLTemplateHandler;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.templates.HTMLTemplateManager;
import com.dragome.templates.interfaces.TemplateManager;

public class GuiaServiceLocator
{
	protected static GuiaServiceLocator instance;
	protected TemplateManager templateManager;

	public static GuiaServiceLocator getInstance()
	{
		if (instance == null)
			instance= new GuiaServiceLocator();

		return instance;
	}

	public TemplateManager getTemplateManager()
	{
		if (templateManager == null)
		{
			//			if (isClientSide())
			templateManager= new HTMLTemplateManager();
			//			else
			//				templateManager= getServerSideServiceFactory().createSyncService(TemplateManager.class);
		}

		return templateManager;
	}

	public TemplateHandler getTemplateHandler()
	{
		return new HTMLTemplateHandler();
	}
}
