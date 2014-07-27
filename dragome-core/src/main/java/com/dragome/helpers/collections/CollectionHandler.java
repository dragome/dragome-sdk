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
package com.dragome.helpers.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionHandler<T>
{
	protected T singleItem;
	protected List<T> multipleItems;

	public CollectionHandler()
	{
	}

	public CollectionHandler(List<T> elements)
	{
		setMultipleItems(elements);
	}

	public void add(T item)
	{
		if (singleItem == null && multipleItems == null)
			singleItem= item;
		else
		{
			if (multipleItems == null)
			{
				multipleItems= new ArrayList<T>();
				multipleItems.add(singleItem);
				singleItem= null;
			}
			multipleItems.add(item);
		}
	}

	public void forAll(ItemInvoker<T> invoker)
	{
		if (singleItem != null)
			invoker.invoke(singleItem);
		else if (multipleItems != null)
		{
			for (int i= 0; i < multipleItems.size(); i++)
				invoker.invoke(multipleItems.get(i));
		}
	}

	public List<T> getList()
	{
		if (singleItem != null)
			return Arrays.asList(singleItem);
		else
			return multipleItems != null ? new ArrayList<T>(multipleItems) : new ArrayList<T>();
	}

	public List<T> getMultipleItems()
	{
		return multipleItems;
	}

	public T getSingleItem()
	{
		return singleItem;
	}

	public boolean isEmpty()
	{
		return singleItem == null && multipleItems == null;
	}

	public void remove(T item)
	{
		if (multipleItems == null)
			singleItem= null;
		else
		{
			multipleItems.remove(item);
			if (multipleItems.isEmpty())
				multipleItems= null;
		}
	}

	public void removeAll()
	{
		singleItem= null;
		multipleItems= null;
	}

	public void setMultipleItems(List<T> multipleItems)
	{
		this.multipleItems= multipleItems;
	}
	public void setSingleItem(T singleItem)
	{
		this.singleItem= singleItem;
	}
}
