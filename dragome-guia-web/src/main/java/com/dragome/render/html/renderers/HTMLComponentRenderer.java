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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.w3c.dom.Element;

import com.dragome.guia.components.VisualButtonImpl;
import com.dragome.guia.components.VisualCheckboxImpl;
import com.dragome.guia.components.VisualLabelImpl;
import com.dragome.guia.components.VisualListBoxImpl;
import com.dragome.guia.components.VisualPanelImpl;
import com.dragome.guia.components.VisualRadioButton;
import com.dragome.guia.components.VisualTextFieldImpl;
import com.dragome.guia.components.interfaces.VisualComboBox;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualImage;
import com.dragome.guia.components.interfaces.VisualLink;
import com.dragome.guia.components.interfaces.VisualProgressBar;
import com.dragome.guia.components.interfaces.VisualTextArea;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.ContentImpl;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Template;

public class HTMLComponentRenderer implements ComponentRenderer<Element, VisualComponent>
{
	private static Map<Class<? extends VisualComponent>, Class<? extends ComponentRenderer<Element, ? extends VisualComponent>>> renderers= new HashMap<Class<? extends VisualComponent>, Class<? extends ComponentRenderer<Element, ? extends VisualComponent>>>();

	{
		addRenderFor(VisualImage.class, HTMLImageRenderer.class);
		addRenderFor(VisualLink.class, HTMLLinkRenderer.class);
		addRenderFor(VisualComboBox.class, HTMLComboBoxRenderer.class);
		addRenderFor(VisualListBoxImpl.class, HTMLListRenderer.class);
		addRenderFor(VisualPanelImpl.class, HTMLPanelRenderer.class);

		addRenderFor(VisualRadioButton.class, HTMLRadioButtonRenderer.class);
		addRenderFor(VisualCheckboxImpl.class, HTMLCheckboxRenderer.class);
		addRenderFor(VisualButtonImpl.class, HTMLButtonRenderer.class);

		addRenderFor(VisualLabelImpl.class, HTMLLabelRenderer.class);
		addRenderFor(VisualTextFieldImpl.class, HTMLTextFieldRenderer.class);
		addRenderFor(VisualTextArea.class, HTMLTextAreaRenderer.class);
		addRenderFor(VisualProgressBar.class, HTMLProgressBarRenderer.class);
	}

	public Canvas render(final VisualComponent visualComponent)
	{
		ComponentRenderer renderer= findComponentRenderer(visualComponent.getClass());
		Canvas render;

		if (renderer != null)
			render= renderer.render(visualComponent);
		else
			render= new HTMLLabelRenderer().render(new VisualLabelImpl("l1", "no renderer!!"));

		if (visualComponent.getLayout() instanceof TemplateLayout)
		{
			TemplateLayout templateLayout= (TemplateLayout) visualComponent.getLayout();

			Template template= templateLayout.getTemplate();
			if (template != null)
				template.updateContent(new ContentImpl<Object>(render.getContent()));
		}

		return render;
	}

	public static ComponentRenderer findComponentRenderer(Class<? extends VisualComponent> componentType)
	{
		Class<? extends ComponentRenderer<Element, ? extends VisualComponent>> class1= renderers.get(componentType);

		ComponentRenderer renderer= null;

		if (class1 != null)
			renderer= ServiceLocator.getInstance().getReflectionService().createClassInstance(class1);

		if (renderer == null)
			for (Entry<Class<? extends VisualComponent>, Class<? extends ComponentRenderer<Element, ? extends VisualComponent>>> entry : renderers.entrySet())
			{
				if (entry.getKey().isAssignableFrom(componentType))
					renderer= ServiceLocator.getInstance().getReflectionService().createClassInstance(entry.getValue());
			}
		return renderer;
	}

	public static <A extends VisualComponent> void addRenderFor(Class<? extends A> componentClass, Class<? extends ComponentRenderer<Element, ? extends A>> rendererClass)
	{
		renderers.put(componentClass, rendererClass);
	}

	public boolean matches(Template child, VisualComponent aVisualComponent)
	{
		ComponentRenderer renderer= findComponentRenderer(aVisualComponent.getClass());
		return renderer != null ? renderer.matches(child) : false;
	}

	@Override
	public boolean matches(Template child)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Optional<Template> findMatchingTemplateFor(Class<? extends VisualComponent> componentType, Template template)
	{
		return null;
	}

	public static Optional<Template> findMatchingTemplateFor(VisualComponent aVisualComponent, Template template)
	{
		return findComponentRenderer(aVisualComponent.getClass()).findMatchingTemplateFor(aVisualComponent.getClass(), template);
	}

	@Override
	public boolean isTemplateCompatible(Template template)
	{
		return matches(template);
	}
}
