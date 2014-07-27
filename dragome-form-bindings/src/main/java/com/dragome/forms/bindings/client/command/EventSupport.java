package com.dragome.forms.bindings.client.command;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 1, 2010
 * Time: 4:11:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventSupport
{
	private EventsImpl alwaysEvents;
	private EventsImpl onNextCallEvents;

	public Events onNextCall()
	{
		if (onNextCallEvents == null)
		{
			onNextCallEvents= new EventsImpl();
		}
		return onNextCallEvents;
	}

	public Events always()
	{
		if (alwaysEvents == null)
		{
			alwaysEvents= new EventsImpl();
		}

		return alwaysEvents;
	}

	public Trigger prepareEvents()
	{
		Trigger trigger= new Trigger(alwaysEvents, onNextCallEvents);
		// our one time events need to be cleared...
		onNextCallEvents= null;

		return trigger;
	}

	public class Trigger
	{
		EventsImpl[] events;

		public Trigger(EventsImpl... events)
		{
			this.events= events;
		}

		public void fireStarted()
		{
			for (EventsImpl event : events)
			{
				if (event != null)
				{
					event.fireStart();
				}
			}
		}

		public void fireFinished()
		{
			for (EventsImpl event : events)
			{
				if (event != null)
				{
					event.fireFinish();
				}
			}

		}

		public LifeCycleCallback asLifeCycleCallback()
		{
			return new LifeCycleCallback()
			{
				public void onStart()
				{
					fireStarted();
				}

				public void onFinish()
				{
					fireFinished();
				}
			};
		}
	}

}
