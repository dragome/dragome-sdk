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
import java.util.LinkedHashSet;
import java.util.Set;

import org.w3c.dom.Element;

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.events.listeners.interfaces.StyleChangedListener;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.model.interfaces.Style;
import com.dragome.services.WebServiceLocator;
import com.dragome.templates.TemplateLayout;

public class HTMLStyleChangedListener implements StyleChangedListener
{
	public HTMLStyleChangedListener()
	{
	}

	public void styleChanged(Style style)
	{
		VisualComponent visualComponent= style.getVisualComponent();
		Element element= findElement(visualComponent);
		if (element != null)
		{
			String name= style.getName();
			if (!style.isSynchronized())
			{
				String attribute= element.getAttribute("class");
				if (attribute == null)
					attribute= "";

				String result= (attribute + (name != null ? " " + name : "")).trim();

				style.setName(deDup(result));
				style.setSynchronized(true);
			}

			if (name != null && name.trim().length() > 0)
				element.setAttribute("class", name);
			else
				element.removeAttribute("class");

			if (style.isEnabled())
				element.removeAttribute("disabled");
			else
				element.setAttribute("disabled", "disabled");
			//			if (style.isVisible())
			//				element.removeAttribute("style");
			//			else
			//				element.setAttribute("style", "display:none;");
		}
	}

	public Element findElement(VisualComponent visualComponent)
	{
		String entityId= DragomeEntityManager.getEntityId(visualComponent);
		Element element;
		if (visualComponent.getLayout() instanceof TemplateLayout)
			element= (Element) ((TemplateLayout) visualComponent.getLayout()).getTemplate().getContent().getValue();
		else
			element= WebServiceLocator.getInstance().getDomHandler().getElementBySelector("[" + AbstractHTMLComponentRenderer.COMPONENT_ID_ATTRIBUTE + "=\"" + entityId + "\"]");
		return element;
	}

	public String deDup(String s)
	{
		String[] strArr= s.split(" ");
		Set<String> set= new LinkedHashSet<String>(Arrays.asList(strArr));
		String[] result= new String[set.size()];
		set.toArray(result);
		StringBuilder res= new StringBuilder();
		for (int i= 0; i < result.length; i++)
		{
			String string= result[i];
			if (i == result.length - 1)
				res.append(string);
			else
				res.append(string).append(" ");
		}
		return res.toString();
	}

	public void boundsChanged(Style style)
	{
		VisualComponent visualComponent= style.getVisualComponent();
		Element element= findElement(visualComponent);
		if (element != null)
		{
			String styleString= "position: relative; left: ${left}px;top: ${top}px;";
			styleString= styleString.replace("${left}", style.getBounds().getX() + "");
			styleString= styleString.replace("${top}", style.getBounds().getY() + "");
			if (element != null)
				element.setAttribute("style", styleString);
		}
	}
}
