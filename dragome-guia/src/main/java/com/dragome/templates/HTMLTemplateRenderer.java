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
package com.dragome.templates;

import org.w3c.dom.Element;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.canvas.interfaces.CanvasHelper;
import com.dragome.templates.interfaces.Content;
import com.dragome.templates.interfaces.Template;

public class HTMLTemplateRenderer
{
	public static final String INNER_HTML= "innerHTML";
	public static final String OUTER_HTML= "outerHTML";
	protected Template template;

	public Template getTemplate()
	{
		return template;
	}

	public void setTemplate(Template template)
	{
		this.template= template;
	}

	protected Canvas canvas;

	public HTMLTemplateRenderer()
	{
	}

	public HTMLTemplateRenderer(Template template)
	{
		this.template= template;
		init();
	}

	public void init()
	{
		Element templateContent= ((Content<Element>) template.getContent()).getValue();
		Element content= (Element) templateContent;

		CanvasHelper canvasHelper= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().getCanvasHelper();
		//	canvasHelper.hideTemplateIds(template);

		Element cloneElement= (Element) content.cloneNode(true);

		//	canvasHelper.restoreTemplateIds(template);

		canvas= GuiaServiceLocator.getInstance().getTemplateManager().getCanvasFactory().createCanvas();

		//	if (template.isInner())
		//	    canvas.setContent(cloneElement.getAttribute(INNER_HTML));
		//	else
		canvas.setContent(cloneElement);
	}

	public void replaceTemplateElement(String anAlias, Canvas aCanvas)
	{
		canvas.replaceSection(anAlias, aCanvas);
	}

	public Canvas getResult()
	{
		return canvas;
	}
}
