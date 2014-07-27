package com.dragome.forms.bindings.client.command;

/**
 * This class is used by {@link com.pietschy.gwt.pectin.client.command.ExceptionManager} to handle
 * various exceptions as they occur.
 */
public abstract class ExceptionHandler<T extends Throwable, E>
{
	private AsyncCommandCallback<?, E> callback;

	public abstract void handle(T error);

	@SuppressWarnings("unchecked")
	void process(Throwable error, AsyncCommandCallback<?, E> callback)
	{
		this.callback= callback;
		handle((T) error);
	}

	public void publishError(E error)
	{
		getCallback().publishError(error);
	}

	public void abort()
	{
		getCallback().abort();
	}

	public AsyncCommandCallback<?, E> getCallback()
	{
		return callback;
	}
}
