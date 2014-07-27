package com.dragome.forms.bindings.client.command;

import com.dragome.forms.bindings.client.binding.Disposable;
import com.dragome.forms.bindings.extra.user.client.Command;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 1, 2010
 * Time: 1:33:31 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Events
{
	Disposable onStartInvoke(Command command);

	Disposable onFinishInvoke(Command command);

	<T> SendToBuilder<T> onStartSend(T value);

	<T> SendToBuilder<T> onFinishSend(T value);

	Disposable sendAllEventsTo(LifeCycleCallback callback);

}
