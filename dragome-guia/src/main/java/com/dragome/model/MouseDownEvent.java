package com.dragome.model;

import com.dragome.model.listeners.MouseEvent;


public class MouseDownEvent implements MouseEvent
{
	private int clientX;
	private int clientY;
	private boolean shiftKey;

	public MouseDownEvent(int clientX, int clientY, boolean shiftKey)
	{
		this.clientX= clientX;
		this.clientY= clientY;
		this.shiftKey= shiftKey;
	}

	public int getClientX()
	{
		return clientX;
	}

	public int getClientY()
	{
		return clientY;
	}

	public boolean isShiftKey()
	{
		return shiftKey;
	}

	public void setClientX(int clientX)
	{
		this.clientX= clientX;
	}
	public void setClientY(int clientY)
	{
		this.clientY= clientY;
	}

	public void setShiftKey(boolean shiftKey)
	{
		this.shiftKey= shiftKey;
	}
}
