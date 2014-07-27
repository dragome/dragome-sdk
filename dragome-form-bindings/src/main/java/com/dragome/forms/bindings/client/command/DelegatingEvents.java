package com.dragome.forms.bindings.client.command;

import com.dragome.forms.bindings.client.binding.Disposable;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Mar 21, 2010
 * Time: 3:20:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DelegatingEvents extends EventsImpl implements Disposable
{
	private Disposable disposable;
	private DelegateCallback callback= new DelegateCallback();

	public DelegatingEvents()
	{
	}

	public DelegatingEvents(Events delegate)
	{
		setDelegate(delegate);
	}

	public void setDelegate(Events delegate)
	{
		if (disposable != null)
		{
			disposable.dispose();
			// we null it or we'll dispose it twice if the
			// new delegate is null.
			disposable= null;
		}

		if (delegate != null)
		{
			disposable= delegate.sendAllEventsTo(callback);
		}
	}

	public void dispose()
	{
		setDelegate(null);
	}

	private class DelegateCallback implements LifeCycleCallback
	{
		public void onStart()
		{
			fireStart();
		}

		public void onFinish()
		{
			fireFinish();
		}
	}
}
