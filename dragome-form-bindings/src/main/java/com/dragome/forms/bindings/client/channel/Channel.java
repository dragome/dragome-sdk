package com.dragome.forms.bindings.client.channel;

import com.dragome.forms.bindings.client.binding.Disposable;
import com.dragome.forms.bindings.client.command.ParameterisedCommand;
import com.dragome.forms.bindings.client.function.Function;
import com.dragome.forms.bindings.client.value.ValueTarget;
import com.dragome.model.interfaces.HasValue;

/**
 * Channels allow objects to be passed between interested parties.  It's a similar concept to an event bus
 * except that it doesn't require the creation of explicit event types.  On the flips side a single Channel
 * is only suitable for simple notifications, i.e. to notify "customer updated" and "customer deleted" events
 * you'd need two Channel&lt;Customer&gt; instances or a Channel&ltCustomerEvent&gt;.
 * <p>
 * The {@link #sendTo(Destination)} style methods bind the channel to the specified recipient.
 */
public interface Channel<T> extends Publisher<T>
{
	Disposable sendTo(Destination<? super T> destination);

	Disposable sendTo(Publisher<? super T> destination);

	Disposable sendTo(ValueTarget<? super T> destination);

	Disposable sendTo(HasValue<? super T> destination);

	Disposable sendTo(ParameterisedCommand<? super T> destination);

	/**
	 * Returns this Channel as a destination.
	 * @return this Channel as a destination.
	 */
	Destination<T> asDestination();

	/**
	 * Returns this Channel as a formatted destination.  This allows you to convert the
	 * output of one channel into the content for this channel.
	 *
	 * <pre>
	 * someChannel.sendTo(thisChannel.formattedWith(myFormat));
	 * </pre>
	 *
	 * @return this Channel as a formatted destination.
	 */
	<S> Destination<S> asFormattedDestination(Function<T, S> function);

	/**
	 * Gets a Publisher instance that uses the specified function to convert the published
	 * value into this channels type.
	 * <pre>
	 * someChannel.sendTo(thisChannel.usingFormat(myFormat));
	 * </pre>
	 * @param function the conversion function.
	 * @return a Publisher instance that converts from the source type to this channels type.
	 */
	<S> Publisher<S> getFormattedPublisher(Function<T, S> function);
}
