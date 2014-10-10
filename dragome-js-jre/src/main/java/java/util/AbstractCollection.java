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
package java.util;

public abstract class AbstractCollection<T> implements Collection<T>, Iterable<T>
{

	public abstract Iterator<T> iterator();

	/**
	 * Returns the number of elements in this collection.
	 */
	public abstract int size();

	/**
	 * Returns an array containing all of the elements in this list in the correct order.
	 */
	public T[] toArray()
	{
		return toArray((T[]) new Object[size()]);
	}

	/**
	 * Returns an array containing all of the elements in this list in the correct order.
	 */
	public <T2> T2[] toArray(T2[] target)
	{
		int size= size();
		if (target.length < size)
			target= (T2[]) new Object[size];
		int i= 0;
		for (T element : this)
		{
			target[i++]= (T2) element;
		}
		if (target.length > size)
			target[size]= null;
		return target;
	}

	/**
	 * Returns a string representation of this collection.
	 */
	public String toString()
	{
		StringBuffer buffer= new StringBuffer();
		buffer.append('[');
		int i= 0;
		for (T element : this)
		{
			if (i++ > 0)
				buffer.append(", ");
			buffer.append(element);
		}
		buffer.append(']');
		return buffer.toString();
	}

	public boolean addAll(Collection<? extends T> c)
	{//TODO revisar
		for (T t : c)
		{
			add(t);
		}
		return true;
	}

	public boolean retainAll(Collection<?> c)
	{
		throw new RuntimeException("not implemented");
	}

	public boolean removeAll(Collection<?> c)
	{
		for (Object object : c)
	        remove(object);
		
		return true;
	}

	public boolean containsAll(Collection<?> c)
	{
		throw new RuntimeException("not implemented");
	}
	
	public boolean remove(Object elem)
	{
		return false;
	}
	
	public boolean isEmpty()
	{
		return true;
	}
	
	public boolean contains(Object value)
	{
		return false;
	}
}
