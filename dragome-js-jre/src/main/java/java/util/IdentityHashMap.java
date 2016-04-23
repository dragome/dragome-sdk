package java.util;

public class IdentityHashMap<K, V> extends HashMap<K, V> implements Map<K, V>, java.io.Serializable, Cloneable
{
	public IdentityHashMap()
	{
	}

	public IdentityHashMap(int i)
	{
		super(i);
	}

	public IdentityHashMap(Map<? extends K, ? extends V> m)
	{
		super(m);
	}
}
