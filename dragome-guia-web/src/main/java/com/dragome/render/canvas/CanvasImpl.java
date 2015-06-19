package com.dragome.render.canvas;

import com.dragome.render.canvas.interfaces.Canvas;

public class CanvasImpl<T> implements Canvas<T>
{
	private T component;

	public void setContent(T object)
	{
		component= object;
	}
	public T getContent()
	{
		return component;
	}
	public void replaceSection(String anAlias, Canvas<?> aCanvas)
	{
	}
}