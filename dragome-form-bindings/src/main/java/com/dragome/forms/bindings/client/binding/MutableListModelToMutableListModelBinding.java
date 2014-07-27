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

import com.dragome.model.interfaces.list.ListModel;
import com.dragome.model.interfaces.list.ListModelChangedEvent;
import com.dragome.model.interfaces.list.ListModelChangedHandler;
import com.dragome.model.interfaces.list.MutableListModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2009
 * Time: 4:35:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class MutableListModelToMutableListModelBinding<T> extends AbstractMutableListBinding<T>
{
	private MutableListModel<T> target;

	public MutableListModelToMutableListModelBinding(MutableListModel<T> source, MutableListModel<T> target)
	{
		super(source);
		this.target= target;
		registerDisposable(target.addListModelChangedHandler(new WidgetMonitor()));
	}

	@Override
	protected void updateTarget(ListModel<T> model)
	{
		target.setElements(model.asUnmodifiableList());
	}

	public MutableListModel<T> getTarget()
	{
		return target;
	}

	private class WidgetMonitor implements ListModelChangedHandler<T>
	{
		public void onListDataChanged(ListModelChangedEvent<T> event)
		{
			updateModel(new MutateOperation<T>()
			{
				public void execute(MutableListModel<T> model)
				{
					model.setElements(target.asUnmodifiableList());
				}
			});
		}
	}
}