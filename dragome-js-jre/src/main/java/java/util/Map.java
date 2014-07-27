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

public interface Map<K, V>
{
	public void clear();
	public boolean containsKey(Object key);
	public boolean containsValue(Object value);
	public V get(Object key);
	public boolean isEmpty();
	public Set<K> keySet();
	public V put(K key, V value);
	public V remove(Object key);
	public int size();
	public Collection<V> values();

	interface Entry<K, V>
	{
		K getKey();
		V getValue();
		V setValue(V value);
		boolean equals(Object o);
		int hashCode();
	}

	Set<Map.Entry<K, V>> entrySet();
}
