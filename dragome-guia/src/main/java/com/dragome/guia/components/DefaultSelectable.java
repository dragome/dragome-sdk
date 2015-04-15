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

import com.dragome.guia.events.listeners.interfaces.SelectionListener;
import com.dragome.model.interfaces.Selectable;

public class DefaultSelectable extends DefaultEventProducer implements Selectable
{
	protected boolean selected= false;

	public DefaultSelectable()
	{
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean isSelected)
	{
		selected= isSelected;
		if (hasListener(SelectionListener.class))
			getListener(SelectionListener.class).selectionChanged(DefaultSelectable.this);
	}

	public void select()
	{
		setSelected(true);
	}

	public void deselect()
	{
		setSelected(false);
	}
}
