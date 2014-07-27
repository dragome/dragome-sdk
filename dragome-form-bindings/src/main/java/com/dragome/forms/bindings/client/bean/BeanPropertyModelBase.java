package com.dragome.forms.bindings.client.bean;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Mar 26, 2010
 * Time: 11:17:24 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BeanPropertyModelBase extends HasDirtyModel
{
	void writeToSource(boolean clearDirtyState);

	void readFromSource();

	void checkpoint();

	void revert();

	boolean isMutable();

	boolean isMutableProperty();

	Class getValueType();
}
