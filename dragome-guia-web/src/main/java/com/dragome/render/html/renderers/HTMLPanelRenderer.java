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
package com.dragome.render.html.renderers;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.render.canvas.HTMLCanvas;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;

public class HTMLPanelRenderer extends AbstractHTMLComponentRenderer<VisualPanel>
{
	public HTMLPanelRenderer()
	{
	}

	public Canvas<Element> render(VisualPanel visualPanel)
	{
		super.render(visualPanel);

		Template template= ((TemplateLayout) visualPanel.getLayout()).getTemplate();

		if (template != null)
		{
			Element element= ((Content<Element>) template.getContent()).getValue();

			addListeners(visualPanel, element);
			//			if (visualPanel.hasListener(ClickListener.class))
			//			{
			//				element.setAttribute("onclick", "_ed.onEvent(event)");
			//				element.setAttribute("onmousedown", "stopEvent(event);");
			//			}

			final String id= DragomeEntityManager.add(visualPanel);

			return new HTMLCanvas(element);
		}

		return null;
	}

	public boolean matches(VisualPanel aVisualComponent, Template child)
	{
	    	List<String> panelTags= Arrays.asList("div", "table", "tr", "th", "td", "tbody");
	    
		Element element= ((Content<Element>) child.getContent()).getValue();

		String nodeName= element.getNodeName();

		return panelTags.contains(nodeName.toLowerCase());

		//		return child.getName().equals(aVisualComponent.getName()) || child.getName().equalsIgnoreCase("panel");
	}
}
