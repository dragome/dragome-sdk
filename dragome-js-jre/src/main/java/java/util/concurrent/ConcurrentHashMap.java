package java.util.concurrent;

import java.io.Serializable;
import java.util.HashMap;

public class ConcurrentHashMap<K, V> extends HashMap<K, V> implements ConcurrentMap<K, V>, Serializable
{
	public V putIfAbsent(K key, V value)
	{
		return put(key, value);
	}

	public boolean remove(Object key, Object value)
	{
		remove(key);
		return true;
	}

	public boolean replace(K key, V oldValue, V newValue)
	{
		replace(key, newValue);
		return true;
	}

	public V replace(K key, V value)
	{
		return put(key, value);
	}
}
