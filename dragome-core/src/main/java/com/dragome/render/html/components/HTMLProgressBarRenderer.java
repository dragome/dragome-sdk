/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.render.html.components;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.VisualProgressBar;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.services.GuiaServiceLocator;
import com.dragome.services.ServiceLocator;

public class HTMLProgressBarRenderer implements ComponentRenderer<Element, VisualProgressBar>
{
	public Canvas<Element> render(final VisualProgressBar progressBar)
	{
		Document document= ServiceLocator.getInstance().getDomHandler().getDocument();
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
		perDiv.setAttribute("innerHTML", progressBar.getPercentage() + "%");
		statDiv.setAttribute("style", "width:" + progressBar.getPercentage() + "%;");
	}
}
