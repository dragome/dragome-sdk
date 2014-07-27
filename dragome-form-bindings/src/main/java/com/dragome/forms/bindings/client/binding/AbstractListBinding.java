package com.dragome.forms.bindings.client.binding;

import com.dragome.model.interfaces.list.ListModel;
import com.dragome.model.interfaces.list.ListModelChangedEvent;
import com.dragome.model.interfaces.list.ListModelChangedHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: May 22, 2010
 * Time: 9:37:05 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractListBinding<T> extends AbstractBinding implements Disposable
{
	private ListModel<T> model;
	private ListMonitor listMonitor= new ListMonitor();

	public AbstractListBinding(ListModel<T> field)
	{
		this.model= field;
		registerDisposable(field.addListModelChangedHandler(listMonitor));
	}

	protected ListModel<T> getModel()
	{
		return model;
	}

	@Override
	public final void updateTarget()
	{
		updateTarget(getModel());
	}

	protected abstract void updateTarget(ListModel<T> model);

	protected void whileIgnoringModelChanges(Runnable r)
	{
		boolean oldIgnoreValue= listMonitor.isIgnoreEvents();
		try
		{
			listMonitor.setIgnoreEvents(true);
			r.run();
		}
		finally
		{
			listMonitor.setIgnoreEvents(oldIgnoreValue);
		}
	}

	private class ListMonitor implements ListModelChangedHandler<T>
	{
		private boolean ignoreEvents= false;

		public void onListDataChanged(ListModelChangedEvent<T> event)
		{
			if (!ignoreEvents)
			{
				updateTarget();
			}
		}

		public void setIgnoreEvents(boolean ignoreEvents)
		{
			this.ignoreEvents= ignoreEvents;
		}

		public boolean isIgnoreEvents()
		{
			return ignoreEvents;
		}
	}
}
