package com.dragome.forms.bindings.client.command;

import com.dragome.forms.bindings.extra.user.client.Command;

/**
 * AbstractUiCommand provides a support for managing enabled state.
 *
 * @see #enableWhen(com.pietschy.gwt.pectin.client.value.ValueModel)
 * @see #disableWhen(com.pietschy.gwt.pectin.client.value.ValueModel)
 */
public abstract class AbstractUiCommand extends UiCommandSupport
{
	private EventSupport eventSupport= new EventSupport();

	public Events always()
	{
		return eventSupport.always();
	}

	public Events onNextCall()
	{
		return eventSupport.onNextCall();
	}

	public final void execute()
	{
		if (!enabled().getValue())
		{
			onDisabledExecution();
		}
		else
		{
			// we prepare the events before the interceptors as this ensures the
			// onNextCall event listeners are cleared.  This is part of the contract
			// with interceptors and onNextCall() events.  i.e. an intercepted and
			// cancelled call still constitutes a call.
			final EventSupport.Trigger trigger= eventSupport.prepareEvents();

			runWithInterceptors(new Command()
			{
				public void execute()
				{
					trigger.fireStarted();

					doExecute();

					trigger.fireFinished();
				}
			});
		}
	}

	protected abstract void doExecute();

	/**
	 * Called when ever execute is invoke while enabled == false;  The default implementation throws
	 * an IllegalStateException.  Subclasses can override as required.
	 */
	protected void onDisabledExecution()
	{
		throw new IllegalStateException("execute called while disabled");
	}
}
