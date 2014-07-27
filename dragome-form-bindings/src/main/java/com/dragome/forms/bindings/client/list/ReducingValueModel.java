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

package com.dragome.forms.bindings.client.list;

import java.util.List;

import com.dragome.forms.bindings.client.function.Reduce;
import com.dragome.forms.bindings.client.value.AbstractReducingValueModel;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.list.ListModel;
import com.dragome.model.interfaces.list.ListModelChangedEvent;
import com.dragome.model.interfaces.list.ListModelChangedHandler;

/**
 * ValueModelFunction is a value model whose value is derived from collection of source
 * {@link com.pietschy.gwt.pectin.client.value.ValueModel}s and a {@link com.pietschy.gwt.pectin.client.function.Reduce}.  Changes in any of the source models result in
 * the function being re-evaluated and the value updating.
 */
public class ReducingValueModel<T, S> extends AbstractReducingValueModel<T, S>
{
	private ListModel<S> source;
	private HandlerRegistration handlerRegistration;

	public ReducingValueModel(ListModel<S> source, Reduce<T, ? super S> function)
	{
		super(function);
		setSource(source);
	}

	private void setSource(ListModel<S> source)
	{
		if (source == null)
		{
			throw new NullPointerException("source is null");
		}

		if (handlerRegistration != null)
		{
			handlerRegistration.removeHandler();
			handlerRegistration= null;
		}

		this.source= source;
		handlerRegistration= source.addListModelChangedHandler(new ListChangeMonitor<S>());

		recompute();
	}

	@Override
	protected List<S> prepareValues()
	{
		return source.asUnmodifiableList();
	}

	private class ListChangeMonitor<S> implements ListModelChangedHandler<S>
	{
		public void onListDataChanged(ListModelChangedEvent<S> sListModelChangedEvent)
		{
			tryRecompute();
		}
	}
}