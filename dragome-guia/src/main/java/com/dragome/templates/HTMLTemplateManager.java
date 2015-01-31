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

import com.dragome.render.canvas.HTMLCanvasFactory;
import com.dragome.render.canvas.interfaces.CanvasFactory;
import com.dragome.render.html.HTMLTemplateHandler;
import com.dragome.render.html.HTMLTemplateLoadingStrategy;
import com.dragome.render.html.components.HTMLComponentRenderer;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateLoadingStrategy;
import com.dragome.templates.interfaces.TemplateManager;

public class HTMLTemplateManager implements TemplateManager
{
	public TemplateHandler getTemplateHandler()
	{
		return new HTMLTemplateHandler();
	}

	public TemplateLoadingStrategy getTemplateHandlingStrategy()
	{
		return new HTMLTemplateLoadingStrategy();
	}

	public CanvasFactory<Element> getCanvasFactory()
	{
		return new HTMLCanvasFactory();
	}

	public Template createTemplate(String aTemplateName)
	{
		Element element= ServiceLocator.getInstance().getDomHandler().getDocument().getElementById(aTemplateName);
		return new HTMLTemplateFactory().createTemplate(element, aTemplateName);
	}

	public Template createTemplateFromElement(Element containerElement, String aName)
	{//TODO arreglar esto!!!!
		return new HTMLTemplateFactory().createTemplate(containerElement, aName);
	}

	public ComponentRenderer getComponentRenderer()
	{
		return new HTMLComponentRenderer();
	}
}
