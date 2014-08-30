/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.forms.bindings.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ObservableList<T> implements List<T>
{
	protected List<T> list;

	public void fireChangeEvent()
	{
		listChangeListener.listChanged();
	}

	protected ListChangedListener listChangeListener= new ListChangedListener()
	{
		public void listChanged()
		{
		}
	};

	public ObservableList(List<T> list)
	{
		this.list= new ArrayList<T>(list);
	}

	public static <S> List<S> makeObservable(List<S> list)
	{
		return new ObservableList<S>(new ArrayList<S>(list));
	}

	public int size()
	{
		return list.size();
	}

	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	public boolean contains(Object o)
	{
		return list.contains(o);
	}

	public Iterator<T> iterator()
	{
		return list.iterator();
	}

	public Object[] toArray()
	{
		return list.toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return list.toArray(a);
	}

	public boolean add(T e)
	{
		boolean result= list.add(e);
		listChangeListener.listChanged();
		return result;
	}

	public boolean remove(Object o)
	{
		boolean result= list.remove(o);
		listChangeListener.listChanged();
		return result;
	}

	public boolean containsAll(Collection<?> c)
	{
		return list.containsAll(c);
	}

	public boolean addAll(Collection<? extends T> c)
	{
		boolean result= list.addAll(c);
		listChangeListener.listChanged();
		return result;
	}

	public boolean addAll(int index, Collection<? extends T> c)
	{
		boolean result= list.addAll(index, c);
		listChangeListener.listChanged();
		return result;
	}

	public boolean removeAll(Collection<?> c)
	{
		boolean result= list.removeAll(c);
		listChangeListener.listChanged();
		return result;
	}

	public boolean retainAll(Collection<?> c)
	{
		boolean retainAll= list.retainAll(c);
		listChangeListener.listChanged();
		return retainAll;
	}

	public void clear()
	{
		list.clear();
		listChangeListener.listChanged();
	}

	public boolean equals(Object o)
	{
		return list.equals(o);
	}

	public int hashCode()
	{
		return list.hashCode();
	}

	public T get(int index)
	{
		return list.get(index);
	}

	public T set(int index, T element)
	{
		T set= list.set(index, element);
		listChangeListener.listChanged();
		return set;
	}

	public void add(int index, T element)
	{
		list.add(index, element);
		listChangeListener.listChanged();
	}

	public T remove(int index)
	{
		T remove= list.remove(index);
		listChangeListener.listChanged();
		return remove;
	}

	public int indexOf(Object o)
	{
		return list.indexOf(o);
	}

	public int lastIndexOf(Object o)
	{
		return list.lastIndexOf(o);
	}

	public ListIterator<T> listIterator()
	{
		return list.listIterator();
	}

	public ListIterator<T> listIterator(int index)
	{
		return list.listIterator(index);
	}

	public List<T> subList(int fromIndex, int toIndex)
	{
		return list.subList(fromIndex, toIndex);
	}

	public void setListChangeListener(ListChangedListener listChangeListener)
	{
		this.listChangeListener= listChangeListener;
	}
}
