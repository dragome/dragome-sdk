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

import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.listeners.ClickListener;

public class SelectableButtonComponent extends SelectableComponent
{
	protected String caption;

	public SelectableButtonComponent(String name, String aCaption)
	{
		this(name);
		caption= aCaption;
	}

	public SelectableButtonComponent()
	{
	}

	public SelectableButtonComponent(String aName)
	{
		super(aName);
	}

	public String getCaption()
	{
		return caption;
	}

	public void setCaption(String caption)
	{
		this.caption= caption;
	}
	
	protected void init()
	{
		setValue(false, false);

		addListener(ClickListener.class, new ClickListener()
		{
			public void clickPerformed(VisualComponent aVisualComponent)
			{
				setValue(!getValue());
			}
		});
	}
}
