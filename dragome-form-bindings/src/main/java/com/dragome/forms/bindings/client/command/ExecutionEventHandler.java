package com.dragome.forms.bindings.client.command;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Mar 21, 2010
 * Time: 3:37:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutionEventHandler<R, E>
{
	void onStarting();
	void onSuccess(R result);
	void onError(E result);
	void onFinished();

}
