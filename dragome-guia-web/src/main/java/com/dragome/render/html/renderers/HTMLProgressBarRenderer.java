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
package com.dragome.render.html.renderers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.guia.components.interfaces.VisualProgressBar;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.services.WebServiceLocator;

public class HTMLProgressBarRenderer extends AbstractHTMLComponentRenderer<VisualProgressBar> implements ComponentRenderer<Element, VisualProgressBar>
{
	public Canvas<Element> render(final VisualProgressBar progressBar)
	{
		Document document= WebServiceLocator.getInstance().getDomHandler().getDocument();
		Element mainDiv= document.createElement("div");
		mainDiv.setAttribute("class", "progressWrap");

		final Element perDiv= document.createElement("div");
		perDiv.setAttribute("class", "progressPer");

		mainDiv.appendChild(perDiv);
		final Element statDiv= document.createElement("div");
		statDiv.setAttribute("class", "progressStat");
		mainDiv.appendChild(statDiv);

		updatePercentage(perDiv, statDiv, progressBar);

		progressBar.addValueChangeHandler(new ValueChangeHandler<Integer>()
		{
			public void onValueChange(ValueChangeEvent<Integer> event)
			{
				updatePercentage(perDiv, statDiv, progressBar);
			}
		});

		Canvas<Element> canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();
		canvas.setContent(mainDiv);

		return canvas;
	}

	private void updatePercentage(final Element perDiv, final Element statDiv, VisualProgressBar progressBar)
	{
		perDiv.setTextContent(progressBar.getPercentage() + "%");
		statDiv.setAttribute("style", "width:" + progressBar.getPercentage() + "%;");
	}
}
