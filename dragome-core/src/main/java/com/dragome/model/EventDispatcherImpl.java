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
package com.dragome.model;

import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.listeners.BlurListener;
import com.dragome.model.listeners.ClickListener;
import com.dragome.model.listeners.DoubleClickListener;
import com.dragome.model.listeners.EventDispatcher;
import com.dragome.model.listeners.InputListener;
import com.dragome.model.listeners.KeyDownListener;
import com.dragome.model.listeners.KeyPressListener;
import com.dragome.model.listeners.KeyUpListener;
import com.dragome.model.listeners.MouseOutListener;
import com.dragome.model.listeners.MouseOverListener;
import com.dragome.remote.entities.DragomeEntityManager;

public class EventDispatcherImpl implements EventDispatcher
{
	private static boolean processingEvent= false;

	public EventDispatcherImpl()
	{
	}

	private synchronized void runOnlySynchronized(Runnable runnable)
	{
		try
		{
			//TODO revisar esto cuando se ejecuta en el cliente, posible freeze!
			if (!processingEvent)
			{
				processingEvent= true;
				runnable.run();
			}
		}
		finally
		{
			processingEvent= false;
		}
	}

	public static VisualComponent getComponentById(String id)
	{
		return ((VisualComponent) DragomeEntityManager.get(id));
	}

	public void eventPerformedById(final String eventType, final String id, final Object arguments)
	{
		runOnlySynchronized(new Runnable()
		{
			public void run()
			{
				VisualComponent visualComponent= getComponentById(id);
				if (visualComponent != null)
				{
					if (eventType.equals("click"))
						visualComponent.getListener(ClickListener.class).clickPerformed(visualComponent);
					else if (eventType.equals("dblclick"))
						visualComponent.getListener(DoubleClickListener.class).doubleClickPerformed(visualComponent);
					else if (eventType.equals("mouseover"))
						visualComponent.getListener(MouseOverListener.class).mouseOverPerformed(visualComponent);
					else if (eventType.equals("mouseout"))
						visualComponent.getListener(MouseOutListener.class).mouseOutPerformed(visualComponent);
					else if (eventType.equals("keyup"))
						visualComponent.getListener(KeyUpListener.class).keyupPerformed(visualComponent, (int) arguments);
					else if (eventType.equals("keydown"))
						visualComponent.getListener(KeyDownListener.class).keydownPerformed(visualComponent, (int) arguments);
					else if (eventType.equals("keypress"))
						visualComponent.getListener(KeyPressListener.class).keypressPerformed(visualComponent, (int) arguments);
					else if (eventType.equals("blur"))
						visualComponent.getListener(BlurListener.class).blurPerformed(visualComponent);
					else if (eventType.equals("input"))
						visualComponent.getListener(InputListener.class).inputPerformed(visualComponent);
				}
			}
		});
	}
}
