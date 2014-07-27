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

package com.dragome.forms.bindings.client.form.validation;

import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.extra.event.shared.HandlerManager;
import com.dragome.model.interfaces.EventHandler;
import com.dragome.model.interfaces.GwtEvent;
import com.dragome.model.interfaces.HandlerRegistration;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Aug 16, 2009
 * Time: 12:57:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractFieldValidator
{
	private HandlerManager handlers= new HandlerManager(this);

	protected boolean conditionSatisfied(ValueModel<Boolean> condition)
	{
		// we're doing this just in case getValue() returns null.
		return Boolean.TRUE.equals(condition.getValue());
	}

	public void fireEvent(GwtEvent<?> event)
	{
		handlers.fireEvent(event);
	}

	protected <T extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<T> type, T handler)
	{
		return handlers.addHandler(type, handler);
	}
}
