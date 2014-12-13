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
package com.dragome.model;

import java.util.List;

import com.dragome.model.interfaces.HasRenderer;
import com.dragome.model.interfaces.VisualButton;
import com.dragome.model.interfaces.VisualCheckbox;
import com.dragome.model.interfaces.VisualComponentFactory;
import com.dragome.model.interfaces.VisualLabel;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.model.interfaces.VisualTextField;

public class DefaultVisualComponentFactory implements VisualComponentFactory
{
	public VisualButton createButton(String aName, String aCaption)
	{
		return new VisualButtonImpl(aName, aCaption);
	}

	public VisualLabel createLabel(String aName, String aText)
	{
		return new VisualLabelImpl(aName, aText);
	}

	public VisualPanel createPanel(String aName)
	{
		return new VisualPanelImpl(aName);
	}

	public VisualTextField createTextField(String aName, String aText)
	{
		return new VisualTextFieldImpl(aName, aText);
	}

	public VisualCheckbox createCheckbox(String aName, String aCaption)
	{
		return new VisualCheckboxImpl(aName, aCaption, Boolean.FALSE);
	}

	public VisualRadioButton createRadioButton(String aName, String aCaption)
	{
		return new VisualRadioButtonImpl(aName, aCaption);
	}

	public HasRenderer createList(String aName, List<String> elements)
	{
		return new VisualListBoxImpl();
	}
}
