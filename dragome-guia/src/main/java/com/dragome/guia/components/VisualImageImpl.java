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
import com.dragome.model.interfaces.VisualImage;
import com.dragome.model.pectin.ComponentWithValueAndRendererImpl;

public class VisualImageImpl extends ComponentWithValueAndRendererImpl<String> implements VisualImage
{
        public VisualImageImpl()
        {
        }
        
	public VisualImageImpl(String aName)
	{
		this(aName, new SimpleRenderer<String>());
	}

	public VisualImageImpl(String aName, String aValue)
	{
		this(aName);
		setValue(aValue);
	}

	public VisualImageImpl(String aName, Renderer<String> renderer)
	{
		super(aName, renderer);
	}
}
