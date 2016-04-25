
package java.util.concurrent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ConcurrentHashMap<K, V> extends HashMap<K, V> implements ConcurrentMap<K, V>, Serializable
{
	public ConcurrentHashMap()
	{
		super();
	}

	public ConcurrentHashMap(int i)
	{
		super(i);
	}

	public ConcurrentHashMap(Map<? extends K, ? extends V> m)
	{
		super(m);
	}

	public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel)
	{
		super(initialCapacity);
	}

	private static final long serialVersionUID= 7249069246763182397L;

	public V putIfAbsent(K key, V value)
	{
		if (!containsKey(key))
			return put(key, value);
		else
			return get(key);
	}

	public boolean remove(Object key, Object value)
	{
		if (containsKey(key) && get(key).equals(value))
		{
			remove(key);
			return true;
		}
		else
			return false;
	}

	public boolean replace(K key, V oldValue, V newValue)
	{
		if (containsKey(key) && get(key).equals(oldValue))
		{
			put(key, newValue);
			return true;
		}
		else
			return false;
	}

	public V replace(K key, V value)
	{
		if (containsKey(key))
		{
			return put(key, value);
		}
		else
			return null;
	}
}
