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

import java.util.function.Predicate;
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

	default boolean removeIf(Predicate<? super E> filter)
	{
		Objects.requireNonNull(filter);
		boolean removed= false;
		final Iterator<E> each= iterator();
		while (each.hasNext())
		{
			if (filter.test(each.next()))
			{
				each.remove();
				removed= true;
			}
		}
		return removed;
	}
}
