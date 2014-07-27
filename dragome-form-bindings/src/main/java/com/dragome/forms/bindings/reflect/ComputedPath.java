package com.dragome.forms.bindings.reflect;

import com.dragome.forms.bindings.client.bean.Path;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jun 25, 2010
 * Time: 9:48:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class ComputedPath implements Path
{
	private String parentPath;
	private String propertyName;
	private String fullPath;

	public ComputedPath(String path)
	{
		if (path == null)
		{
			throw new NullPointerException("path is null");
		}

		// todo, should really make this a regex.
		if (path.isEmpty())
		{
			throw new IllegalArgumentException("path is empty");
		}

		if (path.startsWith("."))
		{
			throw new IllegalArgumentException("path cant' start with '.'");
		}
		if (path.endsWith("."))
		{
			throw new IllegalArgumentException("path cant' end with '.'");
		}

		this.fullPath= path;
		int lastDot= path.lastIndexOf('.');
		parentPath= lastDot < 0 ? null : path.substring(0, lastDot);
		propertyName= lastDot < 0 ? path : path.substring(lastDot + 1);
	}

	public String getParentPath()
	{
		return parentPath;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public String getFullPath()
	{
		return fullPath;
	}

	public boolean isTopLevel()
	{
		return parentPath == null;
	}
}
