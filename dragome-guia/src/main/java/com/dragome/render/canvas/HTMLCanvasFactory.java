/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.render.canvas;

import org.w3c.dom.Element;

import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.canvas.interfaces.CanvasFactory;
import com.dragome.render.canvas.interfaces.CanvasHelper;
import com.dragome.render.canvas.interfaces.CanvasRenderer;
import com.dragome.render.canvas.interfaces.ConcatCanvas;

public class HTMLCanvasFactory implements CanvasFactory<Element>
{
	public Canvas<Element> createCanvas()
	{
		return new HTMLCanvas();
	}

	public ConcatCanvas<Element> createConcatCanvas(boolean horizontal)
	{
		return new HTMLConcatCanvas(horizontal);
	}

	public CanvasRenderer<Element> createCanvasRenderer()
	{
		return new HTMLCanvasRenderer();
	}

	public CanvasHelper getCanvasHelper()
	{
		return new HTMLCanvasHelper();
	}
}
