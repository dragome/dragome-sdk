package com.dragome.forms.bindings.client.command;

import com.dragome.forms.bindings.client.value.ValueHolder;
import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Mar 25, 2010
 * Time: 2:42:09 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTemporalUiCommand extends UiCommandSupport implements TemporalUiCommand
{
	private ValueHolder<Boolean> active= new ValueHolder<Boolean>(false);

	public ValueModel<Boolean> active()
	{
		return active;
	}

	/**
	 * This is only for use in base type classes, not user end type work.
	 *
	 * @param active <code>true</code> if the command is active, <code>false</code> otherwise.
	 */
	void setActive(boolean active)
	{
		this.active.setValue(active);
	}

	public void execute()
	{
		if (active().getValue())
		{
			// the default implementation throws an {@link ReEntrantExecutionException}.
			onReEntrantExecution();
		}
		else if (!enabled().getValue())
		{
			onDisabledExecution();
		}
		else
		{
			startExecution(new Context());
		}
	}

	/**
	 * Hook for subclasses to perform work just before the command starts.
	 */
	protected void onStarting()
	{
	}

	/**
	 * Hook for subclasses to perform work after the command ends.
	 */
	protected void afterFinish()
	{
	}

	protected abstract void startExecution(Context context);

	/**
	 * Called when ever execute is invoke while active == true.  Subclasses can override as required.
	 */
	protected void onReEntrantExecution()
	{
		throw new ReEntrantExecutionException();
	}

	/**
	 * Called when ever execute is invoke while enabled == false;  The default implementation throws
	 * an ExecutedWhileDisabledException.  Subclasses can override as required.
	 */
	protected void onDisabledExecution()
	{
		throw new ExecutedWhileDisabledException();
	}

	public class Context
	{
		public void notifyStarted()
		{
			setActive(true);
			onStarting();
		}

		public void notifyFinished()
		{
			afterFinish();
			setActive(false);
		}
	}
}
