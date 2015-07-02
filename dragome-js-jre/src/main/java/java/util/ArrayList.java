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

import com.dragome.commons.javascript.ScriptHelper;

/**
 * Resizable-array implementation of the List interface. 
 * 
 * @author j2js.com
 */
public class ArrayList<E> extends AbstractCollection<E> implements List<E>
{

	class ArrayListIterator implements ListIterator<E>
	{
		private int lastReturnedIndex= -1;
		private ArrayList<E> list;
		private int currentIndex;

		public ArrayListIterator(ArrayList<E> theList)
		{
			list= theList;
			currentIndex= 0;
		}

		public ArrayListIterator(ArrayList<E> theList, int index)
		{
			list= theList;
			currentIndex= index;
		}

		public boolean hasNext()
		{
			return list.size() > currentIndex;
		}

		public E next()
		{
			if (!hasNext())
			{
				throw new NoSuchElementException();
			}
			return list.get(lastReturnedIndex= currentIndex++);
		}

		public void remove()
		{
			if (lastReturnedIndex == -1)
			{
				throw new RuntimeException();// TODO: IllegalStateException();
			}
			list.remove(lastReturnedIndex);

		}

		public boolean hasPrevious()
		{
			return currentIndex > 0;
		}

		public E previous()
		{
			return list.get(lastReturnedIndex= currentIndex - 1);
		}

		public int nextIndex()
		{
			return currentIndex + 1;
		}

		public int previousIndex()
		{
			return currentIndex - 1;
		}

		public void set(E e)
		{
			list.set(lastReturnedIndex, e);
		}

		public void add(E e)
		{
			list.add(e);
		}
	}

	private E[] array;
	private int start;
	private int end;
	private ArrayList<E> backingList;

	/**
	 * Constructs an empty vector so that its internal data array has size 10 and its standard capacity increment is zero.
	 *
	 */
	public ArrayList()
	{
		array= (E[]) (new Object[0]);
		start= 0;
		end= array.length;
	}

	/**
	 * Constructs a list containing the elements of the specified collection,
	 * in the order they are returned by the collection's iterator.
	 */
	public ArrayList(Collection<? extends E> list)
	{
		this();
		for (E elem : list)
		{
			add(elem);
		}
	}

	protected ArrayList(ArrayList<E> theBackingList, int theStart, int theEnd)
	{
		this();
		start= theStart;
		end= theEnd;
		backingList= theBackingList;
		array= backingList.array;
	}

	/**
	 * Constructs an empty list with the specified initial capacity.
	 */
	public ArrayList(int initialCapacity)
	{
		this();
		initialCapacity++;
	}

	/**
	 * Appends the specified element to the end of this list.
	 */
	public boolean add(E element)
	{
		ScriptHelper.put("element", element, this);
		ScriptHelper.eval("this.$$$array.push(element)", this);
		end++;
		return true;
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 */
	public void add(int index, E element)
	{
		if (index < 0 || index > end - start)
		{
			throw new ArrayIndexOutOfBoundsException(index);
		}
		ScriptHelper.put("index", index, this);
		ScriptHelper.put("element", element, this);
		ScriptHelper.eval("this.$$$array.splice(index, 0, element)", this);
		end++;
		// for (int i=array.length; i>index; i--) {
		//     array[i] = array[i-1];
		// }
		// array[index] = elem;
	}

	/**
	 * Removes all of the elements from this list.
	 */
	public void clear()
	{
		removeRange(0, end - start);
	}

	/**
	 * Returns true if this list contains the specified element.
	 */
	public boolean contains(Object value)
	{
		for (int i= start; i < end; i++)
		{
			// TODO: Do we have to use equals()?
			E e= array[i];

			if (e == value || (e != null && e.equals(value)))
				return true;
		}
		return false;
	}

	/**
	 * Returns an enumeration of the components of this vector.
	 */
	public Iterator<E> iterator()
	{
		return new ArrayListIterator(this);
	}

	/**
	 * Increases the capacity of this ArrayList instance, if necessary, to ensure that it can hold at least the number of elements specified by the minimum capacity argument.
	 */
	public void ensureCapacity(int capacity)
	{
	}

	/**
	 * Compares the specified object with this list for equality.
	 */
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof ArrayList))
			return false;
		ArrayList other= (ArrayList) o;
		if (size() != other.size())
			return false;

		for (int i= 0; i < size(); i++)
		{
			Object e1= get(i);
			Object e2= other.get(i);
			if (!(e1 == null ? e2 == null : e1.equals(e2)))
				return false;
		}

		return true;
	}

	/**
	 * Returns the element at the specified position in this list.
	 */
	public E get(int index)
	{
		if (index < 0 || index >= end - start)
		{
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return array[start + index];
	}

	/**
	 * Searches for the first occurrence of the given argument,
	 * testing for equality using the equals method.
	 */
	public int indexOf(Object elem)
	{
		for (int i= start; i < end; i++)
		{
			if ((elem == null && array[i] == null) || (elem != null && elem.equals(array[i])))
				return i - start;
		}
		return -1;
	}

	/**
	 * Tests if this list has no elements.
	 */
	public boolean isEmpty()
	{
		return start == end;
	}

	/**
	 *  Returns the index of the last occurrence of the specified object in this list.
	 */
	public int lastIndexOf(Object elem)
	{
		for (int i= end; i >= start; i--)
		{
			// TODO: Do we have to use equals()?
			if (elem == array[i])
				return i - start;
		}
		return -1;
	}

	/**
	 * Removes a single instance of the specified element from this collection, if it is present (optional operation).
	 */
	public boolean remove(Object elem)
	{
		int index= indexOf(elem);
		if (index == -1)
			return false;
		remove(index);
		return true;
	}

	/**
	 * Removes the element at the specified position in this list.
	 */
	public E remove(int index)
	{
		E obj= array[index];
		removeRange(index, index + 1);
		return obj;
	}

	/**
	 * Removes from this List all of the elements whose index is between fromIndex,
	 * inclusive and toIndex, exclusive.
	 */
	protected void removeRange(int fromIndex, int toIndex)
	{
		int deleteCount= toIndex - fromIndex;

		if (fromIndex < 0 || toIndex > end - start)
		{
			throw new ArrayIndexOutOfBoundsException();
		}

		int srcPosition= start + fromIndex + deleteCount;
		System.arraycopy(array, srcPosition, array, start + fromIndex, array.length - srcPosition);

		ScriptHelper.put("newSize", array.length - deleteCount, this);
		ScriptHelper.eval("this.$$$array.length = newSize", this);
		incEnd(-deleteCount);
	}

	private void incEnd(int count)
	{
		end+= count;
		if (backingList != null)
			backingList.incEnd(count);
	}

	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 */
	public E set(int index, E elem)
	{
		if (index < 0 || index >= end - start)
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		E old= array[start + index];
		array[start + index]= elem;
		return old;
	}

	/**
	 * Returns the number of elements in this list.
	 */
	public int size()
	{
		return end - start;
	}

	/**
	 * Returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive.
	 */
	public List<E> subList(int fromIndex, int toIndex)
	{
		return new ArrayList<E>(this, fromIndex, toIndex);
	}

	/**
	 * Trims the capacity of this ArrayList instance to be the list's current size.
	 */
	public void trimToSize()
	{
	}

	public ListIterator<E> listIterator()
	{
		return (ListIterator<E>) new ArrayListIterator(this);
	}

	public ListIterator<E> listIterator(int index)
	{
		return (ListIterator<E>) new ArrayListIterator(this, index);
	}

	private void rangeCheckForAdd(int index)
	{
		if (index > size() || index < 0)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private String outOfBoundsMsg(int index)
	{
		return "Index: " + index + ", Size: " + size();
	}

	public boolean addAll(int index, Collection<? extends E> c)
	{
		Object[] a= c.toArray();
		int numNew= a.length;

		int numMoved= size() - index;
		if (numMoved > 0)
			System.arraycopy(array, index, array, index + numNew, numMoved);

		System.arraycopy(a, 0, array, index, numNew);
		end+= numNew;
		return numNew != 0;
	}

	public boolean addAll(Collection<? extends E> c)
	{
		for (E e : c)
			add(e);

		return c.size() > 0;
	}
}
