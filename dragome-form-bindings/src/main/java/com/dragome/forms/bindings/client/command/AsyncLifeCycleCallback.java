package com.dragome.forms.bindings.client.command;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 1, 2010
 * Time: 2:13:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AsyncLifeCycleCallback<R, E> extends LifeCycleCallback
{
	public void onSuccess(R result);
	public void onError(E error);
}
