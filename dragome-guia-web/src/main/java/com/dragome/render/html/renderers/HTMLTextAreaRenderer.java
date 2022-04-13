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

import org.w3c.dom.Element;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.interfaces.VisualTextArea;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.render.canvas.interfaces.Canvas;

public class HTMLTextAreaRenderer extends AbstractHTMLComponentRenderer<VisualTextArea<Object>>
{
	public HTMLTextAreaRenderer()
	{
	}

	public Canvas<Element> render(final VisualTextArea<Object> visualTextArea)
	{
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();
		canvas.setContent(new MergeableElement(this)
		{
			public void mergeWith(final Element textAreaElement)
			{
				final String id= DragomeEntityManager.add(visualTextArea);

				visualTextArea.addValueChangeHandler(new ValueChangeHandler<Object>()
				{
					public void onValueChange(ValueChangeEvent<Object> event)
					{
						setElementInnerHTML(textAreaElement, (String) event.getValue());
					}
				});

				String value= visualTextArea.getRenderer().render(visualTextArea.getValue());
				textAreaElement.setAttribute("onchange", "EventDispatcher.setText(this.id, this.value);stopEvent(event);");

				setElementInnerHTML(textAreaElement, value);
			}
		});

		return canvas;
	}
}
