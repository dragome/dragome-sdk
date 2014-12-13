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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Element;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualListBox;
import com.dragome.model.listeners.ClickListener;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.GuiaServiceLocator;
import com.dragome.templates.HTMLTemplateRenderer;

public class HTMLListRenderer extends AbstractHTMLComponentRenderer<VisualListBox<Object>>
{
	public HTMLListRenderer()
	{
	}

	public Canvas<Element> render(final VisualListBox<Object> visualList)
	{
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		canvas.setContent(new MergeableElement()
		{
			public void mergeWith(final Element selectElement)
			{
				final String id= DragomeEntityManager.add(visualList);

				//	visualList.addValueChangeHandler(new ValueChangeHandler<Object>()
				//	{
				//	    public void onValueChange(ValueChangeEvent<Object> event)
				//	    {
				//		final DomHandler domHandler= ServiceLocator.getInstance().getDomHandler();
				//		Element elementById= domHandler.getDocument().getElementById(id);
				//		elementById.setAttribute("value", (String) event.getValue());
				//	    }
				//	});
				//		final Element selectElement= ServiceLocator.getInstance().getDomHandler().getDocument().createElement("select");

				visualList.addValueChangeHandler(new ValueChangeHandler<Object>()
				{
					public void onValueChange(ValueChangeEvent<Object> event)
					{
						if (event.getValue() instanceof Collection)
							selectValue(selectElement, (Collection) event.getValue());
						else
							selectValue(selectElement, Arrays.asList(event.getValue()));
					}

					private void selectValue(final Element selectElement, Collection<Object> values)
					{
						Renderer<Object> renderer= visualList.getRenderer();
						ScriptHelper.put("renderedValues", new Object(), this);
						ScriptHelper.eval("renderedValues={}", this);

						for (Object object : values)
						{
							ScriptHelper.put("value", renderer.render(object), this);
							ScriptHelper.evalNoResult("renderedValues[value]=true", this);
						}

						ScriptHelper.put("sel", selectElement, this);
						ScriptHelper.evalNoResult("for(var opt, j = 0; opt = sel.node.options[j]; j++) {opt.selected= renderedValues[opt.value]; }", this);
					}
				});

				visualList.addListener(ClickListener.class, new ClickListener()
				{
					public void clickPerformed(VisualComponent aVisualComponent)
					{
						ScriptHelper.put("e", selectElement, this);
						if (ScriptHelper.evalBoolean("e.node.multiple", this))
						{
							String values= (String) ScriptHelper.eval("(function (){for (var options = e.node.options, values= [], i = 0, len = options.length; i < len;i++)  if (options[i].selected) values.push(options[i].value); return values.join(',')})()", this);
							visualList.setSelectedValues((List) Arrays.asList(values.split(",")));
						}
						else
						{
							String value= (String) ScriptHelper.eval("e.node.options[e.node.selectedIndex].value", this);

							Renderer<Object> renderer= visualList.getRenderer();

							Collection<Object> acceptableValues= visualList.getAcceptableValues();
							for (Object object : acceptableValues)
							{
								String render= renderer.render(object);
								if (render.equals(value))
									visualList.setValue(object);
							}
						}
					}
				});

				selectElement.setAttribute("size", getSelectElementSize() + "");
//				selectElement.setAttribute("style", "min-width:300px;");

				if (visualList.isMultipleItems())
					selectElement.setAttribute("multiple", "multiple");

				int i= 1;
				String options= "";
				for (Object element : visualList.getAcceptableValues())
				{
					Renderer<Object> renderer= visualList.getRenderer();
					String rendered= renderer.render(element);
					Object value= visualList.getValue();
					String selected= "";

					boolean isSelected= visualList.isMultipleItems() && visualList.getSelectedValues().contains(element);
					isSelected|= !visualList.isMultipleItems() && element.equals(value);

					if (isSelected)
						selected= "selected=\"selected\"";

					String option= "<option value=\"" + rendered + "\" " + selected + ">" + rendered + "</option>\n";
					options+= option;
				}
				selectElement.setAttribute(HTMLTemplateRenderer.INNER_HTML, options);

				addListeners(visualList, selectElement);
			}
		});

		return canvas;

	}
	protected int getSelectElementSize()
	{
		return 5;
	}
}
