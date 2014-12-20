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

public class HashMap<K, V> implements Map<K, V>
{
	private int i;

	public class DefaultEntry implements java.util.Map.Entry
	{
		private K k;

		public DefaultEntry(K k)
		{
			this.k= k;
		}
		public Object getKey()
		{
			return k;
		}
		public Object getValue()
		{
			return get(k);
		}
		public Object setValue(Object value)
		{
			return put(k, (V) value);
		}
	}

	public HashMap(Map<? extends K, ? extends V> m)
	{
		this();
		for (K key : m.keySet())
		{
			put(key, m.get(key));
		}
	}

	public HashMap()
	{
		ScriptHelper.evalNoResult("this.obj = new Hashtable(_ed.hashCodeFunction, _ed.equalsFunction)", null);
	}

	public HashMap(int i)
	{
		this.i= i;
	}

	/**
	 * Removes all mappings from this map.
	 */
	public void clear()
	{
		ScriptHelper.evalNoResult("this.obj.clear()", null);
	}

	/**
	 * Returns true if this map contains a mapping for the specified key.
	 */
	public boolean containsKey(Object key)
	{
		ScriptHelper.put("key", getRealKey(key), null);
		return ScriptHelper.evalBoolean("this.obj.containsKey (key)", null);
	}

	/**
	 * Returns true if this map maps one or more keys to the specified value.
	 */
	public boolean containsValue(Object value)
	{
		ScriptHelper.put("value", value, null);
		return ScriptHelper.evalBoolean("this.obj.containsValue(value)", null);
	}

	/**
	 * Returns the value to which the specified key is mapped in this identity hash map,
	 * or null if the map contains no mapping for this key.
	 */
	public V get(Object key)
	{
		ScriptHelper.put("key", getRealKey(key), null);
		Object value= ScriptHelper.eval("this.obj.get (key)", null);
		return (V) value;
	}

	private Object getRealKey(Object key)
	{
		if (key != null)
			return key;
		else
			return "_null_";
	}

	/**
	 * Returns true if this map contains no key-value mappings.
	 */
	public boolean isEmpty()
	{
		return size() == 0;
	}

	/**
	 * Returns a set view of the keys contained in this map.
	 */
	public HashSet<K> keySet()
	{
		HashSet<K> set= new HashSet<K>();
		Object value= ScriptHelper.eval("this.obj.entries()", null);

		ScriptHelper.put("entries", value, null);
		int length= ScriptHelper.evalInt("entries.length", null);

		for (int i= 0; i < length; i++)
		{
			ScriptHelper.put("i", i, null);
			Object entryKey= ScriptHelper.eval("entries[i][0]", null);
			set.add((K) entryKey);
		}
		return (HashSet<K>) set;
	}

	/**
	 * Associates the specified value with the specified key in this map.
	 */
	public V put(K key, V value)
	{
		ScriptHelper.put("key", getRealKey(key), null);
		ScriptHelper.put("value", value, null);
		Object oldValue= ScriptHelper.eval("this.obj.put (key, value)", null);
		return (V) oldValue;
	}

	/**
	 * Removes the mapping for this key from this map if present.
	 */
	public V remove(Object key)
	{
		ScriptHelper.put("key", getRealKey(key), null);
		Object oldValue= ScriptHelper.eval("this.obj.remove (key)", null);
		return (V) oldValue;
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 */
	public int size()
	{
		int size= ScriptHelper.evalInt("this.obj.size()", null);
		return size;
	}

	/**
	 * Returns a string representation of this map.
	 */
	public String toString()
	{
		return "";
	}

	/**
	 * Returns a collection view of the values contained in this map.
	 */
	public Collection<V> values()
	{
		HashSet<K> keySet= keySet();
		List<V> list= new ArrayList<V>();
		ArrayList<K> keys= new ArrayList<K>(keySet);
		for (K key : keys)
			list.add(get(key));

		return list;
	}

	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		Set<java.util.Map.Entry<K, V>> result= new HashSet<Map.Entry<K, V>>();
		HashSet<K> keySet= keySet();

		for (final K k : keySet)
			result.add(new DefaultEntry(k));

		return result;
	}

	public void putAll(Map<? extends K, ? extends V> oldMap)
	{
		for (Entry<? extends K, ? extends V> entry : oldMap.entrySet())
			put(entry.getKey(), entry.getValue());
	}
}
