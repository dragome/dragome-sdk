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

public interface List<E> extends Collection<E>
{
	int size();

	boolean isEmpty();

	boolean contains(Object o);

	Iterator<E> iterator();

	Object[] toArray();

	<T> T[] toArray(T[] a);

	boolean add(E e);

	boolean remove(Object o);

	boolean containsAll(Collection<?> c);

	boolean addAll(Collection<? extends E> c);

	boolean addAll(int index, Collection<? extends E> c);

	boolean removeAll(Collection<?> c);

	boolean retainAll(Collection<?> c);

	void clear();

	boolean equals(Object o);

	int hashCode();

	E get(int index);

	E set(int index, E element);

	void add(int index, E element);

	E remove(int index);

	int indexOf(Object o);

	int lastIndexOf(Object o);

	ListIterator<E> listIterator();

	ListIterator<E> listIterator(int index);

	List<E> subList(int fromIndex, int toIndex);
}
