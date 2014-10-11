/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.render.html.components;

import org.w3c.dom.Element;

import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.VisualLabel;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.GuiaServiceLocator;
import com.dragome.templates.HTMLTemplateRenderer;

public class HTMLLabelRenderer extends AbstractHTMLComponentRenderer<VisualLabel<Object>>
{
	public HTMLLabelRenderer()
	{
	}

	public Canvas<Element> render(final VisualLabel<Object> visualLabel)
	{
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		canvas.setContent(new MergeableElement()
		{
			private String originalAttribute;

			public void mergeWith(final Element labelElement)
			{
				String id= DragomeEntityManager.add(visualLabel);
				//final Element labelElement= ServiceLocator.getInstance().getDomHandler().getDocument().createElement("span");
				setInnerText(visualLabel, labelElement);

				visualLabel.addValueChangeHandler(new ValueChangeHandler<Object>()
				{
					public void onValueChange(ValueChangeEvent<Object> event)
					{
						setInnerText(visualLabel, labelElement);
					}
				});

				addListeners(visualLabel, labelElement);
			}

			private void setInnerText(VisualLabel<Object> visualLabel, Element label1)
			{
				Renderer<Object> renderer= visualLabel.getRenderer();
				String aText= renderer.render(visualLabel.getValue());

				String attribute= label1.getAttribute("data-attribute-name");
				if (attribute != null && attribute.length() > 0)
				{
					String template= label1.getAttribute("data-attribute-template-" + attribute);
					if (originalAttribute == null)
						originalAttribute= template;

					String replaceAll= originalAttribute.replaceAll("\\$\\{template:[^\\}]+}", aText);
					label1.setAttribute(attribute, replaceAll);
				}
				else if (aText != null && !aText.trim().isEmpty())
					label1.setAttribute(HTMLTemplateRenderer.INNER_HTML, aText == null ? "null" : aText);
			}
		});

		return canvas;
	}

}
