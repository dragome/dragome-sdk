package com.dragome.web.html.dom.html5canvas.interfaces;

import org.w3c.dom.html.HTMLElement;

public interface HTMLCanvasElement extends HTMLElement, CanvasImageSource
{
	int getWidth();
	void setWidth(int width);
	int getHeight();
	void setHeight(int height);
	CanvasRenderingContext2D getContext(String contextId);
	void setCoordinateSpaceWidth(int canvasWidth);
	void setCoordinateSpaceHeight(int canvasHeight);
}