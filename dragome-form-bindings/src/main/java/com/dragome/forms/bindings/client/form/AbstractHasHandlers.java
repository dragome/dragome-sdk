/*
 * Copyright 2009 Andrew Pietsch 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 *      
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions 
 * and limitations under the License. 
 */

package com.dragome.forms.bindings.client.form;

import com.dragome.forms.bindings.extra.event.shared.HandlerManager;
import com.dragome.model.interfaces.EventHandler;
import com.dragome.model.interfaces.GwtEvent;
import com.dragome.model.interfaces.HandlerRegistration;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 10, 2009
 * Time: 11:39:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractHasHandlers
{
	private HandlerManager handlers= new HandlerManager(this);

	protected boolean areEqual(Object a, Object b)
	{
		return a == null ? b == null : a.equals(b);
	}

	public void fireEvent(GwtEvent<?> event)
	{
		handlers.fireEvent(event);
	}

	protected <H extends EventHandler> HandlerRegistration addHandler(H handler, GwtEvent.Type<H> type)
	{
		return handlers.addHandler(type, handler);
	}
}
