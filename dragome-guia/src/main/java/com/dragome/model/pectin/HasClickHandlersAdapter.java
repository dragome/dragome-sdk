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
package com.dragome.model.pectin;

import com.dragome.model.interfaces.ClickEvent;
import com.dragome.model.interfaces.ClickHandler;
import com.dragome.model.interfaces.GwtEvent;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.HasClickHandlers;
import com.dragome.model.interfaces.HasEnabled;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.listeners.ClickListener;

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
