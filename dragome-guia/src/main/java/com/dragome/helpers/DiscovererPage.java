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

import com.dragome.annotations.PageAlias;
import com.dragome.guia.GuiaVisualActivity;
import com.dragome.guia.components.VisualLabelImpl;
import com.dragome.guia.components.VisualLinkImpl;
import com.dragome.guia.components.VisualPanelImpl;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualLink;
import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.guia.listeners.ClickListener;
import com.dragome.render.ItemProcessorImpl;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.TemplateRepeater;
import com.dragome.templates.interfaces.Template;
import com.dragome.view.VisualActivity;

@PageAlias(alias= "discoverer")
public class DiscovererPage extends GuiaVisualActivity
{
	public void build()
	{
		loadMainTemplate("dragome-resources/html/discover");
		final Template template= mainTemplate;

		String requestURL= ServiceLocator.getInstance().getParametersHandler().getRequestURL();
		final String requestURL2= requestURL.substring(0, requestURL.indexOf("?") + 1);

		final RequestUrlActivityMapper requestUrlActivityMapper= serviceFactory.createSyncService(RequestUrlActivityMapper.class);

		List<Class<? extends VisualActivity>> visualActivities= requestUrlActivityMapper.getExistingVisualActivities();

		new TemplateRepeater<Class<? extends VisualActivity>>().repeatItems(visualActivities, new ItemProcessorImpl<Class<? extends VisualActivity>>(template, "row")
		{
			public void fillTemplates(final Class<? extends VisualActivity> visualActivity, List<Template> aRowTemplate)
			{
				VisualLink link= new VisualLinkImpl("view", visualActivity.getSimpleName());
				link.addClickListener(new ClickListener()
				{
					public void clickPerformed(VisualComponent aVisualComponent)
					{
						openActivity(ServiceLocator.getInstance().getReflectionService().createClassInstance(visualActivity));
					}
				});

				String alias= requestUrlActivityMapper.getActivityAlias(visualActivity);

				VisualPanel rowPanel= new VisualPanelImpl(aRowTemplate.get(0));
				rowPanel.addChild(new VisualLabelImpl<String>("classname", visualActivity.getName()));
				String href= requestURL2 + (alias.length() == 0 ? visualActivity.getSimpleName() : alias);
				VisualLink link2= new VisualLinkImpl("link", href, href);
				rowPanel.addChild(link2);
				rowPanel.addChild(new VisualLabelImpl<String>("alias", alias));
				rowPanel.addChild(link);
			}
		});
	}
}
