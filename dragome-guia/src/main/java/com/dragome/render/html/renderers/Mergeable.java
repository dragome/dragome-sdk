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

import java.util.Optional;

import org.w3c.dom.Element;

import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.templates.interfaces.Template;

public interface Mergeable<T>
{
	public void mergeWith(T element);

	ComponentRenderer<T, ?> getRenderer();
	
	public default Element findCompatibleElement(Template template, Element element)
	{
		ComponentRenderer<T, ?> renderer= getRenderer();
		
		boolean templateCompatible= renderer.isTemplateCompatible(template);

		Template t2= template;
		if (!templateCompatible)
		{
			Optional<Template> findFirst= template.getChildren().stream().filter(t -> renderer.isTemplateCompatible(t)).findFirst();
			if (findFirst.isPresent())
				t2= findFirst.get();
		}

		final Element labelElement= templateCompatible ? element : (Element) t2.getContent().getValue();
		return labelElement;
	}
}
