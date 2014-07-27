package com.dragome.forms.bindings.client.command;

import com.dragome.forms.bindings.client.binding.Disposable;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Mar 21, 2010
 * Time: 3:20:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DelegatingAsyncEvents<R, E> extends AsyncEventsImpl<R, E> implements Disposable
{
	private Disposable disposable;
	private AsyncLifeCycleCallback<R, E> callback= new DelegateCallback();

	public DelegatingAsyncEvents()
	{
	}

	public DelegatingAsyncEvents(AsyncEvents<R, E> source)
	{
		setDelegate(source);
	}

	public void setDelegate(AsyncEvents<R, E> source)
	{
		if (disposable != null)
		{
			disposable.dispose();
			disposable= null;
		}

		if (source != null)
		{
			disposable= source.sendAllEventsTo(callback);
		}

	}

	public void dispose()
	{
		setDelegate(null);
	}

	private class DelegateCallback implements AsyncLifeCycleCallback<R, E>
	{
		public void onStart()
		{
			fireStart();
		}

		public void onSuccess(R result)
		{
			fireSuccess(result);
		}

		public void onError(E error)
		{
			fireError(error);
		}

		public void onFinish()
		{
			fireFinish();
		}
	}
}
