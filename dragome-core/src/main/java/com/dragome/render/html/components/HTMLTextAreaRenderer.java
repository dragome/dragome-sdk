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

import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.VisualTextArea;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.GuiaServiceLocator;

public class HTMLTextAreaRenderer extends AbstractHTMLComponentRenderer<VisualTextArea<Object>>
{
	public HTMLTextAreaRenderer()
	{
	}

	public Canvas<Element> render(final VisualTextArea<Object> visualTextArea)
	{
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();
		canvas.setContent(new MergeableElement()
		{
			public void mergeWith(final Element textAreaElement)
			{
				final String id= DragomeEntityManager.add(visualTextArea);

				visualTextArea.addValueChangeHandler(new ValueChangeHandler<Object>()
				{
					public void onValueChange(ValueChangeEvent<Object> event)
					{
						textAreaElement.setAttribute("innerHTML", (String) event.getValue());
					}
				});

				String value= visualTextArea.getRenderer().render(visualTextArea.getValue());
				textAreaElement.setAttribute("onchange", "EventDispatcher.setText(this.id, this.value);stopEvent(event);");

				textAreaElement.setAttribute("innerHTML", value);
			}
		});

		return canvas;
	}
}
