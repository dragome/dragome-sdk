/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.forms.bindings.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ObservableList<T> implements List<T>
{
	protected List<T> list;

	protected ListChangedListener listChangeListener= new ListChangedListener<T>()
	{
		public void listChanged(T t)
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
		listChangeListener.listChanged(e);
		return result;
	}

	public boolean remove(Object o)
	{
		boolean result= list.remove(o);
		listChangeListener.listChanged(o);
		return result;
	}

	public boolean containsAll(Collection<?> c)
	{
		return list.containsAll(c);
	}

	public boolean addAll(Collection<? extends T> c)
	{
		boolean result= list.addAll(c);
		listChangeListener.listChanged(null);
		return result;
	}

	public boolean addAll(int index, Collection<? extends T> c)
	{
		boolean result= list.addAll(index, c);
		listChangeListener.listChanged(null);
		return result;
	}

	public boolean removeAll(Collection<?> c)
	{
		boolean result= list.removeAll(c);
		listChangeListener.listChanged(null);
		return result;
	}

	public boolean retainAll(Collection<?> c)
	{
		boolean retainAll= list.retainAll(c);
		listChangeListener.listChanged(null);
		return retainAll;
	}

	public void clear()
	{
		list.clear();
		listChangeListener.listChanged(null);
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
		listChangeListener.listChanged(element);
		return set;
	}

	public void add(int index, T element)
	{
		list.add(index, element);
		listChangeListener.listChanged(element);
	}

	public T remove(int index)
	{
		T remove= list.remove(index);
		listChangeListener.listChanged(remove);
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
	
	public String toString()
	{
		return list.toString();
	}
}
