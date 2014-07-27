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

package com.dragome.forms.bindings.client.value;

import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
* User: andrew
* Date: Aug 5, 2009
* Time: 2:22:31 PM
* To change this template use File | Settings | File Templates.
*/
public abstract class GuardedValueChangeHandler<T> implements ValueChangeHandler<T>
{
	private boolean ignoreEvents= false;

	public void setIgnoreEvents(boolean ignoreEvents)
	{
		this.ignoreEvents= ignoreEvents;
	}

	public boolean isIgnoreEvents()
	{
		return ignoreEvents;
	}

	public void onValueChange(ValueChangeEvent<T> event)
	{
		if (!ignoreEvents)
		{
			onGuardedValueChanged(event);
		}
	}

	public abstract void onGuardedValueChanged(ValueChangeEvent<T> event);

	public void whileIgnoringEvents(Runnable r)
	{
		boolean oldIgnore= isIgnoreEvents();
		setIgnoreEvents(true);
		try
		{
			r.run();
		}
		finally
		{
			setIgnoreEvents(oldIgnore);
		}
	}

}
