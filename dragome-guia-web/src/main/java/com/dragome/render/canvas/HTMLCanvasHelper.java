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

import com.dragome.render.canvas.interfaces.CanvasHelper;
import com.dragome.templates.HTMLTemplateFactory;
import com.dragome.templates.interfaces.Template;

public class HTMLCanvasHelper implements CanvasHelper
{
	public void hideTemplateIds(Template template)
	{
		throw new RuntimeException("not implemented");
		//		String key= null;
		//		String result= "";
		//
		//		Document document= WebServiceLocator.getInstance().getDomHandler().getDocument();
		//		for (Entry<String, Template> entry : template.getChildrenMap().entrySet())
		//		{
		//			if (key != null)
		//				result+= ",";
		//
		//			key= entry.getKey();
		//			Template value= entry.getValue();
		//
		//			Attr id= ((Content<Element>) value.getContent()).getValue().getAttributeNode("id");
		//			String value2= id.getValue();
		//			document.getElementById(value2).setAttribute("id", System.identityHashCode(template) + "_" + value2);
		//		}
	}

	public void restoreTemplateIds(Template template)
	{
	}

	public void removeReplacedElements(Element element)
	{
		List<Element> elementByClassMatching= HTMLTemplateFactory.getTemplateElements(element, ".+", true);
		for (Element element2 : elementByClassMatching)
		{
			String attribute= element2.getAttribute("data-rendered");
			if ("".equals(attribute))
				element2.getParentNode().removeChild(element2);
		}
	}

}
