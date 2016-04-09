package java.util;

/**
 * This class implements the Set interface, backed by a {@link java.util.HashMap}.
 *
 *
 */
public class HashSet<E> extends AbstractCollection<E> implements Set<E>
{

	private List<E> list;

	/**
	 * Constructs a new, empty set
	 */
	public HashSet()
	{
		list= new ArrayList<E>();
	}

	public HashSet(Collection<?> source)
	{
		this();
		for (Object object : source)
		{
			add((E) object);
		}
	}

	public HashSet(int n)
	{
	}

	/**
	 * Adds the specified element to this set if it is not already present.
	 */
	public boolean add(E element)
	{
		if (!list.contains(element))
		{
			list.add(element);
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Removes all of the elements from this collection (optional operation).
	 */
	public void clear()
	{
		list.clear();
	}

	/**
	 * Returns true if this set contains the specified element.
	 */
	public boolean contains(Object element)
	{
		return list.contains(element);
	}

	/**
	 * Returns an iterator over the elements in this set.
	 */
	public Iterator<E> iterator()
	{
		return list.iterator();
	}

	/**
	 * Returns true if this collection contains no elements.
	 */
	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	/**
	 * Removes all of the elements from this collection (optional operation).
	 */
	public boolean remove(Object elem)
	{
		return list.remove(elem);
	}

	/**
	 * Returns the number of elements in this set (its cardinality).
	 */
	public int size()
	{
		return list.size();
	}

}
