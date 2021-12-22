
package java.util.concurrent.atomic;

public class AtomicBoolean implements java.io.Serializable
{
	private static final long serialVersionUID= 4654671469794556979L;

	private volatile int value;

	public AtomicBoolean(boolean initialValue)
	{
		value= initialValue ? 1 : 0;
	}

	public AtomicBoolean()
	{
	}

	public final boolean get()
	{
		return value != 0;
	}

	public final boolean compareAndSet(boolean expect, boolean update)
	{
		value= update ? 1 : 0;
		return true;
	}

	public boolean weakCompareAndSet(boolean expect, boolean update)
	{
		return compareAndSet(expect, update);
	}

	public final void set(boolean newValue)
	{
		value= newValue ? 1 : 0;
	}

	public final void lazySet(boolean newValue)
	{
		compareAndSet(true, newValue);
	}

	public final boolean getAndSet(boolean newValue)
	{
		boolean prev;
		do
		{
			prev= get();
		}
		while (!compareAndSet(prev, newValue));
		return prev;
	}
	
	public String toString()
	{
		return Boolean.toString(get());
	}
}
