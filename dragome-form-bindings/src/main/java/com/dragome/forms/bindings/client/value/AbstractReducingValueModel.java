package com.dragome.forms.bindings.client.value;

import java.util.List;

import com.dragome.forms.bindings.client.function.Reduce;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: May 9, 2010
 * Time: 10:10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractReducingValueModel<T, S> extends AbstractValueModel<T>
{
	protected Reduce<T, ? super S> function;
	private T computedValue= null;
	private boolean ignoreChanges= false;

	public AbstractReducingValueModel(Reduce<T, ? super S> function)
	{
		if (function == null)
		{
			throw new NullPointerException("function is null");
		}

		this.function= function;
	}

	public Reduce<T, ? super S> getFunction()
	{
		return function;
	}

	public void setFunction(Reduce<T, ? super S> function)
	{
		if (function == null)
		{
			throw new NullPointerException("function is null");
		}

		this.function= function;

		// we use tryRecompute so we only recompute if we're not ignoring
		// changes for now.
		tryRecompute();
	}

	protected void tryRecompute()
	{
		if (!ignoreChanges)
		{
			recompute();
		}
	}

	protected void recompute()
	{
		T old= computedValue;
		computedValue= computeValue();
		fireValueChangeEvent(old, computedValue);
	}

	T computeValue()
	{
		return function.compute(prepareValues());
	}

	protected abstract List<S> prepareValues();

	public T getValue()
	{
		return computedValue;
	}

	protected boolean isIgnoreChanges()
	{
		return ignoreChanges;
	}

	protected void setIgnoreChanges(boolean ignoreChanges)
	{
		this.ignoreChanges= ignoreChanges;
	}

	/**
	 * Delays re-computation of the result until after the specified runnable
	 * has been completed.  This method is re-entrant.
	 *
	 * @param r the runnable to run.
	 */
	public void recomputeAfterRunning(Runnable r)
	{
		boolean oldValue= isIgnoreChanges();
		try
		{
			setIgnoreChanges(true);
			r.run();
		}
		finally
		{
			setIgnoreChanges(oldValue);
			// we could have been called in a re-entrant mode so we only
			// try and recompute.
			tryRecompute();
		}

	}
}
