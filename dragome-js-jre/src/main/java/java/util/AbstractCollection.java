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
	public Object[] toArray()
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
