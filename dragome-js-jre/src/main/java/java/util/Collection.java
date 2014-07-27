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

import java.util.stream.Stream;

public interface Collection<E> extends Iterable<E>
{
	default Stream<E> stream()
	{
		return new StreamImpl<E, E>(this);
	}

	public boolean add(E element);
	public void clear();
	public boolean contains(Object value);
	public boolean equals(Object o);
	public boolean isEmpty();
	public Iterator<E> iterator();
	public boolean remove(Object elem);
	public int size();
	public Object[] toArray();
	public <T> T[] toArray(T[] target);
	boolean addAll(Collection<? extends E> c);
	boolean containsAll(Collection<?> c);
	boolean removeAll(Collection<?> c);
	boolean retainAll(Collection<?> c);
}
