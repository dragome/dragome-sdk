package com.dragome.forms.bindings.client.bean;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 29, 2010
 * Time: 4:27:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractPropertyDescriptor implements PropertyDescriptor
{
	private final String fullPath;
	private final String parentPath;
	private final String propertyName;
	private final Class beanType;
	private final boolean mutable;

	/**
	 * Base class for PropertyDescriptor instances.
	 *
	 *
	 * @param fullPath     the full property path.  For top level paths this will be the same as the propertyName.
	 * @param parentPath   the path less the property name.  For top level paths this must be null.
	 * @param propertyName the property name.
	 * @param beanType     the type of the bean that defines this property.
	 * @param mutable      <code>true</code> if the property is mutable.
	 */
	protected AbstractPropertyDescriptor(String fullPath, String parentPath, String propertyName, Class beanType, boolean mutable)
	{
		this.fullPath= fullPath;
		this.parentPath= parentPath;
		this.propertyName= propertyName;
		this.beanType= beanType;
		this.mutable= mutable;
	}

	public String getFullPath()
	{
		return fullPath;
	}

	public String getParentPath()
	{
		return parentPath;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public boolean isTopLevel()
	{
		return parentPath == null;
	}

	public Class getBeanType()
	{
		return beanType;
	}

	public boolean isMutable()
	{
		return mutable;
	}
}
