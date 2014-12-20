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
package com.dragome.render.html.components;

import org.w3c.dom.Element;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.interfaces.VisualLink;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.model.interfaces.Renderer;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.templates.HTMLTemplateRenderer;

public class HTMLLinkRenderer extends AbstractHTMLComponentRenderer<VisualLink>
{
	public HTMLLinkRenderer()
	{
	}

	public Canvas<Element> render(final VisualLink visualLink)
	{
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		canvas.setContent(new MergeableElement()
		{
			public void mergeWith(final Element linkElement)
			{
				String id= DragomeEntityManager.add(visualLink);
				if (visualLink.getUrl() != null)
					linkElement.setAttribute("href", visualLink.getUrl());

				setInnerText(visualLink, linkElement);

				addListeners(visualLink, linkElement);
			}

			private void setInnerText(VisualLink visualLink, Element label1)
			{
				if (visualLink.getValue() != null)
				{
					Renderer<String> renderer= visualLink.getRenderer();
					String aText= renderer.render(visualLink.getValue());
					label1.setAttribute(HTMLTemplateRenderer.INNER_HTML, aText == null ? "null" : aText);
				}
			}
		});

		return canvas;
	}

}
