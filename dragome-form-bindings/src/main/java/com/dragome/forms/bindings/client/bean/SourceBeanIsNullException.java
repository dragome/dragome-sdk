package com.dragome.forms.bindings.client.bean;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: May 15, 2010
 * Time: 11:54:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class SourceBeanIsNullException extends RuntimeException
{
	public SourceBeanIsNullException(Path path)
	{
		super("Can't write property: " + path.getFullPath() + " as property: " + path.getParentPath() + " is null");
	}
}
