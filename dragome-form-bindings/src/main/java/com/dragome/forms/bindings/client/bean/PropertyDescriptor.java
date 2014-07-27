package com.dragome.forms.bindings.client.bean;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 19, 2010
 * Time: 12:39:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PropertyDescriptor extends Path
{
	Class getBeanType();

	Class getValueType();

	Class getElementType() throws NotCollectionPropertyException;

	boolean isCollection();

	boolean isMutable();

	Object readProperty(Object bean);

	void writeProperty(Object bean, Object value) throws ReadOnlyPropertyException, TargetBeanIsNullException;
}
