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

import com.dragome.guia.components.interfaces.VisualButton;
import com.dragome.guia.events.listeners.interfaces.ClickListener;

public class VisualButtonImpl extends SelectableButtonComponent implements VisualButton
{
	public VisualButtonImpl()
	{
	}

	public VisualButtonImpl(String aName)
	{
		super(aName, "");
	}

	public VisualButtonImpl(String aName, String aCaption)
	{
		super(aName, aCaption);
	}

	public VisualButtonImpl(String aName, String aCaption, ClickListener clickListener)
	{
		super(aName, aCaption);
		addClickListener(clickListener);
	}

	public VisualButtonImpl(String aName, ClickListener clickListener)
	{
		this(aName, "", clickListener);
	}
}
