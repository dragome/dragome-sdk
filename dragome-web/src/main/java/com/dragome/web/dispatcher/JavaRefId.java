package com.dragome.web.dispatcher;

import com.dragome.helpers.DragomeEntityManager;

public class JavaRefId
{
	private String id;

	public JavaRefId()
	{
	}

	public JavaRefId(String id)
	{
		this.setId(id);
	}

	public Object resolveJavaObject()
	{
		return DragomeEntityManager.get(getId());
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
}
