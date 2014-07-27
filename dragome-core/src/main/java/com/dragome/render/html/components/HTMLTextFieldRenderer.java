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

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualTextField;
import com.dragome.model.listeners.FocusListener;
import com.dragome.model.listeners.InputListener;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.ServiceLocator;

public class HTMLTextFieldRenderer extends AbstractHTMLComponentRenderer<VisualTextField<Object>>
{
	public HTMLTextFieldRenderer()
	{
	}

	public Canvas<Element> render(final VisualTextField<Object> visualTextField)
	{
		Canvas<Element> canvas= ServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

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
						String value= (String) event.getValue();
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
				textFieldElement.setAttribute(COMPONENT_ID_ATTRIBUTE, id);

				ScriptHelper.put("value", value == null ? "" : value, this);
				ScriptHelper.put("textFieldElement", textFieldElement, this);
				ScriptHelper.evalNoResult("textFieldElement.node.value=value", this);

				addListeners(visualTextField, textFieldElement);
			}
		});

		return canvas;
	}

}
