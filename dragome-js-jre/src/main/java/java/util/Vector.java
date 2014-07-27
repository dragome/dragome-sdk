package java.util;

/*
 * Copyright (c) 2005 j2js.com,
 *
 * All Rights Reserved. This work is distributed under the j2js Software License [1]
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.j2js.com/license.txt
 */
public class Vector<E> extends ArrayList<E>
{
	private Object[] array= new Object[0];

	/**
	 * Constructs an empty vector so that its internal data array has size 10 and its standard capacity increment is zero.
	 *
	 */
	public Vector()
	{
	}

	/**
	 * Constructs an empty vector with the specified initial capacity and with its capacity increment equal to zero.
	 */
	public Vector(int initialCapacity)
	{
		initialCapacity++;
	}

	/**
	 * Constructs an empty vector with the specified initial capacity and capacity increment.
	 */
	public Vector(int initialCapacity, int capacityIncrement)
	{
		initialCapacity++;
		capacityIncrement++;
	}

	public Vector(Collection<E> collection)
	{
		for (E element : collection)
		{
			add(element);
		}
	}

	/**
	 * Returns the current capacity of this vector.
	 */
	public int capacity()
	{
		return size();
	}

	/**
	 * Copies the components of this vector into the specified array.
	 */
	public synchronized void copyInto(Object[] target)
	{
		toArray((E[]) target);
	}

	/**
	 * Returns an enumeration of the components of this vector.
	 */
	public synchronized Enumeration elements()
	{
		return Collections.enumeration(this);
	}

	/**
	 * Returns the first component (the item at index 0) of this vector.
	 */
	public synchronized Object firstElement()
	{
		return get(0);
	}

	/**
	 * Searches for the first occurence of the given argument, beginning the search at index, and testing for equality using the equals method.
	 */
	public synchronized int indexOf(Object elem, int index)
	{
		for (int i= index; i < array.length; i++)
		{
			if (elem.equals(array[i]))
				return i;
		}
		return -1;
	}

	/**
	 *  Inserts the specified object as a component in this vector at the specified index.
	 */
	public synchronized void insertElementAt(E elem, int index)
	{
		add(index, elem);
	}

	/**
	 * Returns the last component of the vector.
	 */
	public synchronized E lastElement()
	{
		return get(size() - 1);
	}

	/**
	 * Searches backwards for the specified object, starting from the specified index, and returns an index to it.
	 */
	public synchronized int lastIndexOf(Object elem, int index)
	{
		for (int i= index; i >= 0; i--)
		{
			if (elem == array[i])
				return i;
		}
		return -1;
	}

	/**
	 * Sets the component at the specified index of this vector to be the specified object.
	 */
	public synchronized void setElementAt(E elem, int index)
	{
		set(index, elem);
	}

	/**
	 * Sets the size of this vector.
	 */
	public synchronized void setSize(int newSize)
	{
		removeRange(newSize, size());
	}

	public synchronized void addElement(E obj)
	{
		add(obj);
	}

}
