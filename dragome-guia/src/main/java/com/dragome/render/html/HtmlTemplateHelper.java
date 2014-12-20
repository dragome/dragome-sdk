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
package com.dragome.render.html;

import java.util.List;

import org.w3c.dom.Element;

import com.dragome.render.DomHelper;
import com.dragome.services.ServiceLocator;

public class HtmlTemplateHelper
{
	public static void removeElementsMatchingAttribute(Element root, String name, String value)
	{
		List<Element> elements= HTMLSearchUtils.findElementsForClass(root, name, value);
		for (Element element : elements)
			DomHelper.removeFromParent(element);
	}

	public static String getHtmlPart(String templateName, String id)
	{
		String templateContent= ServiceLocator.getInstance().getRequestExecutor().executeSynchronousRequest("" + templateName, null);
		return HTMLHelper.getTemplatePart(templateContent, id);
	}
}
