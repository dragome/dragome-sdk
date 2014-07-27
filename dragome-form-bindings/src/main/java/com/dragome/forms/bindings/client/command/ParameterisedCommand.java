package com.dragome.forms.bindings.client.command;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Feb 28, 2010
 * Time: 7:27:50 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ParameterisedCommand<T>
{
	public void execute(T value);
}
