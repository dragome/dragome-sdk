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
package com.dragome.model.pectin;

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.events.listeners.interfaces.ClickListener;
import com.dragome.model.interfaces.ClickEvent;
import com.dragome.model.interfaces.ClickHandler;
import com.dragome.model.interfaces.GwtEvent;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.HasClickHandlers;
import com.dragome.model.interfaces.HasEnabled;

public class HasClickHandlersAdapter implements HasClickHandlers, HasEnabled
{
	private VisualComponent visualComponent;

	public HasClickHandlersAdapter(VisualComponent visualComponent)
	{
		this.visualComponent= visualComponent;
	}

	public void fireEvent(GwtEvent<?> event)
	{
		if (visualComponent.hasListener(ClickListener.class))
			visualComponent.getListener(ClickListener.class).clickPerformed(visualComponent);
	}

	public HandlerRegistration addClickHandler(final ClickHandler handler)
	{
		visualComponent.addListener(ClickListener.class, new ClickListener()
		{
			public void clickPerformed(VisualComponent aVisualComponent)
			{
				handler.onClick(new ClickEvent(aVisualComponent));
			}
		});
		return new DummyHandlerRegistration();
	}

	public void setEnabled(boolean enabled)
	{
		visualComponent.getStyle().setEnabled(enabled);
	}

	public boolean isEnabled()
	{
		return visualComponent.getStyle().isEnabled();
	}

}
