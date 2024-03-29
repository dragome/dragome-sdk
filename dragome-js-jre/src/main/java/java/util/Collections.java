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

public class Collections
{
	public static <E> List<E> checkedList(List<E> list, Class<E> type)
	{
		return list;
	}

	private static final class EmptySet extends HashSet
	{
		private static final long serialVersionUID= 1582296315990362920L;

		public boolean contains(Object object)
		{
			return false;
		}

		public int size()
		{
			return 0;
		}

		public Iterator iterator()
		{
			return new Iterator()
			{
				public boolean hasNext()
				{
					return false;
				}

				public Object next()
				{
					throw new NoSuchElementException();
				}

				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}

		private Object readResolve()
		{
			return Collections.EMPTY_SET;
		}
	}

	public static final Set EMPTY_SET= new EmptySet();

	private static class IteratorEnumarator<E> implements Enumeration<E>
	{

		Iterator<E> iterator;

		IteratorEnumarator(Iterator<E> theIterator)
		{
			iterator= theIterator;
		}

		public boolean hasMoreElements()
		{
			return iterator.hasNext();
		}

		public E nextElement()
		{
			if (!iterator.hasNext())
			{
				throw new NoSuchElementException();
			}
			return iterator.next();
		}
	}

	/**
	 * Returns an enumeration over the specified collection. This provides interoperatbility with legacy APIs that require an enumeration as input.
	 */
	public static Enumeration enumeration(Collection c)
	{
		return new IteratorEnumarator(c.iterator());
	}

	private static Random r= new Random();

	/**
	 * Randomly permutes the specified list using a default source of randomness.
	 */
	public static void shuffle(List<?> list)
	{
		int size= list.size();
		for (int i= size; i > 1; i--)
		{
			swap(list, i - 1, r.nextInt(i));
		}
	}

	/**
	 *  Sorts the specified list into ascending order, according to the natural ordering of its elements.
	 *  <br/>
	 *  <b>Important</b>: This is a restriction of the Java API sort(List<T>) function.
	 */
	public static void sort(List<String> list)
	{
		String[] array= (String[]) list.toArray();
		Arrays.sort(array);
		int count= array.length;
		list.clear();
		for (int i= 0; i < count; i++)
		{
			list.add(array[i]);
		}
	}

	/**
	 * Swaps the two specified elements in the specified array.
	 */
	public static void swap(List<?> list, int i, int j)
	{
		final List l= list;
		l.set(i, l.set(j, l.get(i)));
	}

	public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> map)
	{
		if (map == null)
		{
			throw new NullPointerException();
		}
		return (Map<K, V>) map;
	}

	public static final <K, V> Map<K, V> emptyMap()
	{
		return (Map<K, V>) new HashMap<K, V>();
	}

	public static final <T> List<T> emptyList()
	{
		return (List<T>) new ArrayList<T>();
	}

	public static final <T> Set<T> emptySet()
	{
		return (Set<T>) EMPTY_SET;
	}

	public static <T> List<T> unmodifiableList(List<? extends T> list)
	{
		return (List<T>) list;
	}

	public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> collection)
	{
		return (Collection<T>) collection;
	}

	private static final int REVERSE_THRESHOLD= 18;

	public static void reverse(List<?> list)
	{
		int size= list.size();
		if (size < REVERSE_THRESHOLD || list instanceof RandomAccess)
		{
			for (int i= 0, mid= size >> 1, j= size - 1; i < mid; i++, j--)
				swap(list, i, j);
		}
		else
		{
			// instead of using a raw type here, it's possible to capture
			// the wildcard but it will require a call to a supplementary
			// private method
			ListIterator fwd= list.listIterator();
			ListIterator rev= list.listIterator(size);
			for (int i= 0, mid= list.size() >> 1; i < mid; i++)
			{
				Object tmp= fwd.next();
				fwd.set(rev.previous());
				rev.set(tmp);
			}
		}
	}

	public static <T> void sort(List<T> list, Comparator<? super T> c)
	{
		Object[] a= list.toArray();
		Arrays.sort(a, (Comparator) c);
		ListIterator i= list.listIterator();
		for (int j= 0; j < a.length; j++)
		{
			i.next();
			i.set(a[j]);
		}
	}

	public static <T> List<T> synchronizedList(List<T> list)
	{
		return list;
	}

	public static <T> boolean addAll(Collection<? super T> c, T... elements)
	{
		boolean result= false;
		for (T element : elements)
			result|= c.add(element);
		return result;
	}
}
