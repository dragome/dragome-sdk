package com.dragome.forms.bindings.client.command;

import com.dragome.forms.bindings.client.interceptor.Interceptor;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.extra.user.client.Command;

/**
 * A UiCommand is a {@link Command} that also knows it's enabled state and can be bound to buttons.
 * @see #enabled()
 * @see TemporalUiCommand
 * @see AsyncUiCommand
 * @see AbstractUiCommand
 * @see IncrementalUiCommand
 * @see DelegatingUiCommand
 */
public interface UiCommand extends Command
{
	/**
	 * Returns a ValueModel representing the enabled state of this command.
	 * @return a ValueModel representing the enabled state of this command.
	 */
	ValueModel<Boolean> enabled();

	/**
	 * Adds an interceptor to run before the command executes.  The interceptor <b>must</b> invoke
	 * {@link com.pietschy.gwt.pectin.client.interceptor.Invocation#proceed()} or execute {@link com.pietschy.gwt.pectin.client.interceptor.Invocation#getProceedCommand()}
	 * for the execution to proceed.  Failure to do so will effectively abort the execution.  In reality
	 * it's a case of a silent abort since the command is not notified of the interception.
	 * <p>
	 * As such it it imperative that interceptors be tested to ensure they always execute the proceed
	 * command if they don't want the execution to simple stop without further ado.
	 * <p>
	 * Of special interest is the case where onNextCall().onXyz(..) has been used and an interceptor
	 * chooses not to proceed.  In this case the events will never be fired.
	 * <p>
	 * Notes to self:  In order for other parties to be notified of cancellation then the Invocation would need
	 * to provide a cancel or abort method.  The main issue with this is that failing to call it will still abort
	 * the execution.  I.e. abort is equivalent to not calling proceed.  There are a pile of bugs waiting to happen
	 * if I add an Invocation.abort() methods and onAborting(..) style events when I can't guarantee they'll be called.
	 *
	 * @param interceptor the interceptor to add.
	 */
	void interceptUsing(Interceptor interceptor);

	Events always();

	Events onNextCall();
}
