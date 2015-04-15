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

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualTextField;
import com.dragome.guia.events.listeners.interfaces.FocusListener;
import com.dragome.guia.events.listeners.interfaces.InputListener;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.render.canvas.interfaces.Canvas;

public class HTMLTextFieldRenderer extends AbstractHTMLComponentRenderer<VisualTextField<Object>>
{
	public HTMLTextFieldRenderer()
	{
	}

	public Canvas<Element> render(final VisualTextField<Object> visualTextField)
	{
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		canvas.setContent(new MergeableElement()
		{
			public void mergeWith(final Element textFieldElement)
			{
				final String id= DragomeEntityManager.add(visualTextField);

				//				final DomHandler domHandler= ServiceLocator.getInstance().getDomHandler();
				//				final Element textFieldElement= domHandler.getDocument().createElement("input");

				visualTextField.addValueChangeHandler(new ValueChangeHandler<Object>()
				{
					public void onValueChange(ValueChangeEvent<Object> event)
					{
						String value= visualTextField.getRenderer().render(event.getValue());
						textFieldElement.setAttribute("value", value);
						ScriptHelper.put("textFieldElement", textFieldElement, this);
						ScriptHelper.put("value", value, this);
						ScriptHelper.evalNoResult("textFieldElement.node.value=value", this);
					}
				});

				visualTextField.addListener(InputListener.class, new InputListener()
				{
					public void inputPerformed(VisualComponent visualComponent)
					{
						ScriptHelper.put("textFieldElement", textFieldElement, this);
						String value= (String) ScriptHelper.eval("textFieldElement.node.value", this);
						visualTextField.setValue(value);
					}
				});

				visualTextField.addListener(FocusListener.class, new FocusListener()
				{
					public void focusGained(VisualComponent component)
					{
						ScriptHelper.put("textFieldElement", textFieldElement, this);
						ScriptHelper.evalNoResult("textFieldElement.node.focus()", this);
					}

					public void focusLost(VisualComponent component)
					{
					}
				});

				String value= visualTextField.getRenderer().render(visualTextField.getValue());
				textFieldElement.setAttribute("type", "text");
				textFieldElement.setAttribute("value", value);

				ScriptHelper.put("value", value == null ? "" : value, this);
				ScriptHelper.put("textFieldElement", textFieldElement, this);
				ScriptHelper.evalNoResult("textFieldElement.node.value=value", this);

				addListeners(visualTextField, textFieldElement);
			}
		});

		return canvas;
	}

}
