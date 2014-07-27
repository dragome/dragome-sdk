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

package com.dragome.forms.bindings.client.binding;

import com.dragome.model.interfaces.HasValue;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.list.ListModel;
import com.dragome.model.interfaces.list.MutableListModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:35:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListContainsValueBinding<T> extends AbstractMutableListBinding<T>
{
	private HasValue<Boolean> widget;

	private T value;

	public ListContainsValueBinding(MutableListModel<T> model, HasValue<Boolean> widget, T value)
	{
		super(model);
		this.widget= widget;
		this.value= value;
		registerDisposable(widget.addValueChangeHandler(new WidgetMonitor()));
	}

	protected void updateTarget(ListModel<T> model)
	{
		widget.setValue(model.contains(value), false);
	}

	public HasValue<Boolean> getTarget()
	{
		return widget;
	}

	private class WidgetMonitor implements ValueChangeHandler<Boolean>
	{
		public void onValueChange(final ValueChangeEvent<Boolean> event)
		{
			updateModel(new MutateOperation<T>()
			{
				public void execute(MutableListModel<T> model)
				{
					if (event.getValue())
					{
						model.add(value);
					}
					else
					{
						model.remove(value);
					}
				}
			});
		}
	}
}