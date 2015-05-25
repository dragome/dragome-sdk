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

public class HTMLComponentRenderer implements ComponentRenderer<Element, VisualComponent>
{
	private static Map<Class<? extends VisualComponent>, Class<? extends ComponentRenderer<Element, ? extends VisualComponent>>> renderers= new HashMap<Class<? extends VisualComponent>, Class<? extends ComponentRenderer<Element, ? extends VisualComponent>>>();

	{
		addRenderFor(VisualImage.class, HTMLImageRenderer.class);
		addRenderFor(VisualLink.class, HTMLLinkRenderer.class);
	}

	public Canvas render(final VisualComponent visualComponent)
	{
		ComponentRenderer renderer= null;

		if (visualComponent instanceof VisualComboBox)
			renderer= new HTMLComboBoxRenderer();
		else if (visualComponent instanceof VisualListBoxImpl)
			renderer= new HTMLListRenderer();
		else if (visualComponent instanceof VisualPanelImpl)
			renderer= new HTMLPanelRenderer();
		else if (visualComponent instanceof VisualCheckboxImpl)
			renderer= new HTMLCheckboxRenderer();
		else if (visualComponent instanceof VisualButtonImpl)
			renderer= new HTMLButtonRenderer();
		else if (visualComponent instanceof VisualLabelImpl)
			renderer= new HTMLLabelRenderer();
		else if (visualComponent instanceof VisualTextFieldImpl)
			renderer= new HTMLTextFieldRenderer();
		else if (visualComponent instanceof VisualTextArea)
			renderer= new HTMLTextAreaRenderer();
		else if (visualComponent instanceof VisualRadioButton)
			renderer= new HTMLRadioButtonRenderer();
		else if (visualComponent instanceof VisualProgressBar)
			renderer= new HTMLProgressBarRenderer();

		for (Entry<Class<? extends VisualComponent>, Class<? extends ComponentRenderer<Element, ? extends VisualComponent>>> entry : renderers.entrySet())
		{
			if (entry.getKey().isAssignableFrom(visualComponent.getClass()))
				renderer= ServiceLocator.getInstance().getReflectionService().createClassInstance(entry.getValue());
		}

		if (renderer != null)
			return renderer.render(visualComponent);
		else
			return new HTMLLabelRenderer().render(new VisualLabelImpl("l1", "no renderer!!"));
	}

	public static <A extends VisualComponent> void addRenderFor(Class<A> componentClass, Class<? extends ComponentRenderer<Element, ? extends A>> rendererClass)
	{
		renderers.put(componentClass, rendererClass);
	}
}
