package com.dragome.forms.bindings.client.command;

import com.dragome.forms.bindings.client.binding.Disposable;
import com.dragome.forms.bindings.client.channel.Destination;
import com.dragome.forms.bindings.client.channel.Publisher;
import com.dragome.forms.bindings.client.value.ValueTarget;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Mar 6, 2010
 * Time: 1:08:01 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SendToBuilder<T>
{
	Disposable to(Destination<? super T> destination);

	Disposable to(Publisher<? super T> destination);

	Disposable to(ValueTarget<? super T> destination);

	Disposable to(ParameterisedCommand<? super T> destination);
}
