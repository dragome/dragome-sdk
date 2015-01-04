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
import com.dragome.guia.components.interfaces.VisualCheckbox;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.render.canvas.interfaces.Canvas;

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
