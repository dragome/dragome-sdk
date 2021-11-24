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

import java.util.Iterator;
import java.util.Optional;

import org.w3c.dom.Element;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.interfaces.VisualLabel;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.templates.interfaces.Template;

public class HTMLLabelRenderer extends AbstractHTMLComponentRenderer<VisualLabel<Object>>
{
	public HTMLLabelRenderer()
	{
	}

	public Canvas<Element> render(final VisualLabel<Object> visualLabel)
	{
		super.render(visualLabel);

		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		canvas.setContent(new MergeableElement()
		{
			private String originalAttribute;

			public void mergeWith(Template template, Element labelElement1)
			{
				boolean templateCompatible= isTemplateCompatible(template);
				String id= DragomeEntityManager.add(visualLabel);

				labelElement1.setAttribute(COMPONENT_ID_ATTRIBUTE, id);
				Template t2= template;
				if (!templateCompatible)
				{
					labelElement1.setAttribute(COMPONENT_ID_ATTRIBUTE, "parent:" + id);
					Optional<Template> findFirst= template.getChildren().stream().filter(t -> isTemplateCompatible(t)).findFirst();
					if (findFirst.isPresent())
						t2= findFirst.get();
				}
				else
					labelElement1.setAttribute(COMPONENT_ID_ATTRIBUTE, id);

				final Element labelElement= templateCompatible ? labelElement1 : (Element) t2.getContent().getValue();
				labelElement.setAttribute(COMPONENT_ID_ATTRIBUTE, id);

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
					setElementInnerHTML(label1, aText == null ? "null" : aText);
			}

		});

		return canvas;
	}

	@Override
	public boolean isTemplateCompatible(Template child)
	{
		Element element= (Element) child.getContent().getValue();
		String tagName= element.getTagName();
		boolean result= tagName.equalsIgnoreCase("span") || tagName.equalsIgnoreCase("nobr");
		return result;
	}

}
