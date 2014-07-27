package com.dragome.forms.bindings.client.form.metadata.binding;

/**
* Created by IntelliJ IDEA.
* User: andrew
* Date: Feb 24, 2010
* Time: 4:16:02 PM
* To change this template use File | Settings | File Templates.
*/
public interface ConditionBinderWidgetAction<T>
{
	public void apply(T target, boolean state);
}
