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

import java.util.List;

import org.w3c.dom.Element;

import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.canvas.interfaces.CanvasRenderer;
import com.dragome.services.ServiceLocator;

public class HTMLCanvasRenderer implements CanvasRenderer<Element>
{
	public void render(Canvas<Element> canvas, String aPlaceAlias)
	{
		ServiceLocator.getInstance().getTimeCollector().registerStart("show canvas");
		Element element= ServiceLocator.getInstance().getDomHandler().getDocument().getElementById(aPlaceAlias);
		Object content= canvas.getContent();
		if (content instanceof Element)
		{
			Element element2= (Element) content;
			//ServiceLocator.getInstance().getCanvasFactory().getCanvasHelper().removeReplacedElements(element2);

			element.appendChild(element2);
		}
		else
		{
			List<Element> contents= (List<Element>) content;
			for (Element element2 : contents)
			{
				//ServiceLocator.getInstance().getCanvasFactory().getCanvasHelper().removeReplacedElements(element2);
				element.appendChild(element2);
			}
		}

		ServiceLocator.getInstance().getTimeCollector().registerEnd();
	}
}
