package com.dragome.forms.bindings.client.bean;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: May 15, 2010
 * Time: 11:52:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReadOnlyPropertyException extends RuntimeException
{
	public ReadOnlyPropertyException(Path path)
	{
		super("Property is read only: " + path.getFullPath());
	}
}
