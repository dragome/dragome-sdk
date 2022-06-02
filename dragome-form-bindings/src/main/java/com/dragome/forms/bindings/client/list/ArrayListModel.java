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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.dragome.forms.bindings.client.channel.Destination;
import com.dragome.forms.bindings.client.form.AbstractHasHandlers;
import com.dragome.model.interfaces.HandlerRegistration;
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
public class ArrayListModel<T> extends AbstractHasHandlers implements MutableListModel<T>
{
	private ArrayList<T> internalList= new ArrayList<T>();

	public ArrayListModel()
	{
	}

	public ArrayListModel(T... initialValues)
	{
		setElements(Arrays.asList(initialValues));
	}

	public ArrayListModel(Collection<T> initialValues)
	{
		setElements(initialValues);
	}

	public void add(T element)
	{
		internalList.add(element);
		fireListChanged();
	}

	public void remove(T element)
	{
		internalList.remove(element);
		fireListChanged();
	}

	public void setElements(Collection<? extends T> elements)
	{
		boolean equals= internalList.containsAll(elements) && elements.containsAll(internalList);

		if (!equals)
		{
			internalList.clear();
			internalList.addAll(elements);
			fireListChanged();
		}
	}

	public void setElements(T[] elements)
	{
		setElements(Arrays.asList(elements));
	}

	public void clear()
	{
		internalList.clear();
		;
		fireListChanged();
	}

	/**
	 * Returns a {@link Destination} allowing the contents of a {@link Channel} to be
	 * injected directly into the list.
	 * <pre>
	 * channel.sendTo(myList.asDestination());
	 * </pre>
	 * @return a {@link Destination} for this list.
	 */
	public Destination<T[]> asDestination()
	{
		return new Destination<T[]>()
		{
			public void receive(T[] values)
			{
				setElements(values);
			}
		};
	}

	protected void fireListChanged()
	{
		ListModelChangedEvent.fire(this);
	}

	public int size()
	{
		return internalList.size();
	}

	public boolean isEmpty()
	{
		return internalList.isEmpty();
	}

	public T get(int index)
	{
		return internalList.get(index);
	}

	public boolean contains(T element)
	{
		return internalList.contains(element);
	}

	public boolean containsAll(Collection<?> c)
	{
		return internalList.containsAll(c);
	}

	public List<T> asUnmodifiableList()
	{
		return Collections.unmodifiableList(internalList);
	}

	public int indexOf(T value)
	{
		return internalList.indexOf(value);
	}

	public Iterator<T> iterator()
	{
		return internalList.iterator();
	}

	public HandlerRegistration addListModelChangedHandler(ListModelChangedHandler<T> handler)
	{
		return addHandler(handler, ListModelChangedEvent.getType());
	}

	protected boolean isMutableSource()
	{
		return true;
	}
}