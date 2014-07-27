package com.dragome.forms.bindings.client.command;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Feb 28, 2010
 * Time: 10:20:29 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AsyncCommandCallback<R, E>
{
	void publishSuccess(R result);
	void publishError(E error);
	void abort();
}
