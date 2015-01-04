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
package com.dragome.guia.components;

import com.dragome.guia.components.interfaces.VisualCheckbox;
import com.dragome.guia.listeners.ClickListener;

public class VisualCheckboxImpl extends SelectableButtonComponent implements VisualCheckbox
{
	public VisualCheckboxImpl()
    {
		init();
    }
	
	public VisualCheckboxImpl(String aName, String aCaption, Boolean selected)
	{
		super(aName, aCaption);
		setValue(selected);
		init();
	}

	public VisualCheckboxImpl(String aName)
	{
		super(aName);
		init();
	}

	public VisualCheckboxImpl(String aName, String aCaption, ClickListener clickListener)
	{
		super(aName, aCaption);
		init();
		addClickListener(clickListener);
	}

	public VisualCheckboxImpl(String aName, ClickListener clickListener)
	{
		this(aName, "", clickListener);
	}

}
