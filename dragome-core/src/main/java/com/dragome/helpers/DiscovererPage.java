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

import com.dragome.annotations.PageAlias;
import com.dragome.debugging.execution.DragomeVisualActivity;
import com.dragome.model.RequestUrlActivityMapper;
import com.dragome.model.VisualLabelImpl;
import com.dragome.model.VisualLinkImpl;
import com.dragome.model.VisualPanelImpl;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualLink;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.model.listeners.ClickListener;
import com.dragome.render.ItemProcessorImpl;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.TemplateRepeater;
import com.dragome.templates.interfaces.Template;

@PageAlias(alias= "discoverer")
public class DiscovererPage extends DragomeVisualActivity
{
	public void build()
	{
		loadMainTemplate("dragome-resources/html/discover");
		final Template template= mainTemplate;

		String requestURL= ServiceLocator.getInstance().getParametersHandler().getRequestURL();
		final String requestURL2= requestURL.substring(0, requestURL.indexOf("?") + 1);

		final RequestUrlActivityMapper requestUrlActivityMapper= serviceFactory.createSyncService(RequestUrlActivityMapper.class);

		List<Class<? extends DragomeVisualActivity>> visualActivities= requestUrlActivityMapper.getExistingVisualActivities();

		new TemplateRepeater<Class<? extends DragomeVisualActivity>>().repeatItems(visualActivities, new ItemProcessorImpl<Class<? extends DragomeVisualActivity>>(template, "row")
		{
			public void fillTemplates(final Class<? extends DragomeVisualActivity> visualActivity, List<Template> aRowTemplate)
			{
				VisualLink link= new VisualLinkImpl("view", visualActivity.getSimpleName());
				link.addClickListener(new ClickListener()
				{
					public void clickPerformed(VisualComponent aVisualComponent)
					{
						openPage(ServiceLocator.getInstance().getReflectionService().createClassInstance(visualActivity));
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
