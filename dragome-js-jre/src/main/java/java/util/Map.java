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

import java.io.Serializable;

public interface Map<K, V> extends Serializable
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
    void putAll(Map<? extends K, ? extends V> m);
}
