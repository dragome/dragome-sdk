package com.dragome.forms.bindings.client.command;

import com.dragome.forms.bindings.client.binding.Disposable;
import com.dragome.forms.bindings.client.channel.Destination;
import com.dragome.forms.bindings.client.channel.Publisher;
import com.dragome.forms.bindings.client.value.ValueTarget;
import com.dragome.forms.bindings.extra.user.client.Command;

/**
 * This class provides methods to hooking into the events of an {@link AsyncUiCommand}.
 */
public interface AsyncEvents<R, E> extends Events
{
	Disposable sendResultTo(Destination<? super R> destination);

	Disposable sendResultTo(ValueTarget<? super R> destination);

	Disposable sendResultTo(Publisher<? super R> publisher);

	Disposable sendErrorTo(Destination<? super E> destination);

	Disposable sendErrorTo(ValueTarget<? super E> destination);

	Disposable sendErrorTo(Publisher<? super E> publisher);

	Disposable onSuccessInvoke(Command command);

	Disposable onSuccessInvoke(ParameterisedCommand<? super R> command);

	Disposable onErrorInvoke(Command command);

	Disposable onErrorInvoke(ParameterisedCommand<? super E> command);

	<T> SendToBuilder<T> onSuccessSend(T value);

	<T> SendToBuilder<T> onErrorSend(T value);

	Disposable sendAllEventsTo(AsyncLifeCycleCallback<R, E> callback);

}
