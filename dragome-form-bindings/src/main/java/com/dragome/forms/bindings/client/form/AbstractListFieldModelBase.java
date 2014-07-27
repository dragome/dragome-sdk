/*
 * Copyright 2010 Andrew Pietsch
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.list.ListModel;
import com.dragome.model.interfaces.list.ListModelChangedEvent;
import com.dragome.model.interfaces.list.ListModelChangedHandler;
import com.dragome.model.interfaces.list.MutableListModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 2, 2009
 * Time: 11:29:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractListFieldModelBase<T> extends AbstractField<T> implements ListFieldModelBase<T>
{
	private ListModel<T> source;
	private SourceListener<T> sourceListener= new SourceListener<T>();

	public AbstractListFieldModelBase(FormModel formModel, ListModel<T> source, Class<T> valueType)
	{
		super(formModel, valueType);
		if (source == null)
		{
			throw new NullPointerException("source is null");
		}
		this.source= source;
		source.addListModelChangedHandler(sourceListener);
	}

	public void add(T element)
	{
		getMutableSource().add(element);
	}

	public void remove(T element)
	{
		getMutableSource().remove(element);
	}

	public void setElements(Collection<? extends T> elements)
	{
		getMutableSource().setElements(elements);
	}

	public void clear()
	{
		getMutableSource().clear();
	}

	public int size()
	{
		return getSource().size();
	}

	public boolean isEmpty()
	{
		return getSource().isEmpty();
	}

	public T get(int index)
	{
		return getSource().get(index);
	}

	public boolean contains(T element)
	{
		return getSource().contains(element);
	}

	public List<T> asUnmodifiableList()
	{
		return getSource().asUnmodifiableList();
	}

	public int indexOf(T value)
	{
		return getSource().indexOf(value);
	}

	public Iterator<T> iterator()
	{
		return getSource().iterator();
	}

	public HandlerRegistration addListModelChangedHandler(ListModelChangedHandler<T> handler)
	{
		return addHandler(handler, ListModelChangedEvent.getType());
	}

	public boolean isMutableSource()
	{
		return source instanceof MutableListModel;
	}

	public ListModel<T> getSource()
	{
		return source;
	}

	protected MutableListModel<T> getMutableSource()
	{
		verifyMutableSource();
		return (MutableListModel<T>) getSource();
	}

	private class SourceListener<T> implements ListModelChangedHandler<T>
	{
		public void onListDataChanged(ListModelChangedEvent<T> event)
		{
			fireEvent(event);
		}
	}
}