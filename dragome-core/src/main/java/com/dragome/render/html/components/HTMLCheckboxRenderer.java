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
import com.dragome.model.interfaces.VisualCheckbox;
import com.dragome.remote.entities.DragomeEntityManager;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.services.GuiaServiceLocator;

public class HTMLCheckboxRenderer extends AbstractHTMLComponentRenderer<VisualCheckbox>
{
	public Canvas<Element> render(final VisualCheckbox checkbox)
	{
		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		canvas.setContent(new MergeableElement()
		{
			public void mergeWith(final Element button1)
			{
				String id= DragomeEntityManager.add(checkbox);

				button1.setAttribute("type", "checkbox");
				button1.setAttribute("value", checkbox.getCaption());
				updateChecked(checkbox, button1);

				checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>()
				{
					public void onValueChange(ValueChangeEvent<Boolean> event)
					{
						updateChecked(checkbox, button1);
					}
				});
				
				addListeners(checkbox, button1);
			}

			private void updateChecked(final VisualCheckbox checkbox, Element button1)
			{
				boolean checked= checkbox.getValue() != null && checkbox.getValue();
				String isChecked= checked ? "true" : "false";

				ScriptHelper.put("checked", isChecked, this);
				ScriptHelper.put("button1", button1, this);
				ScriptHelper.evalNoResult("button1.node.checked= (checked == 'true')", this);
				//				
				//				if (checked)
				//					button1.setAttribute("checked", "checked");
				//				else
				//					button1.removeAttribute("checked");
			}
		});

		return canvas;
	}
}
