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

import com.dragome.model.interfaces.Renderer;
import com.dragome.model.interfaces.VisualComboBox;

public class VisualComboBoxImpl<T> extends VisualListBoxImpl<T> implements VisualComboBox<T>
{
	public VisualComboBoxImpl()
	{
		super();
	}

	public VisualComboBoxImpl(Iterable<T> acceptableValues)
	{
		super("", acceptableValues);
	}

	public VisualComboBoxImpl(String name, Iterable<T> acceptableValues)
	{
		super(name, acceptableValues);
	}

	public VisualComboBoxImpl(String name, Renderer<T> renderer, Iterable<T> acceptableValues)
	{
		super(name, renderer, acceptableValues);
	}

	public VisualComboBoxImpl(String name, Iterable<T> acceptableValues, T selectedValue)
	{
		this(name, acceptableValues);
		setValue(selectedValue);
	}
}
