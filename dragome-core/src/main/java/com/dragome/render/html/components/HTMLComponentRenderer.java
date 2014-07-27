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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Element;

import com.dragome.model.VisualButtonImpl;
import com.dragome.model.VisualCheckboxImpl;
import com.dragome.model.VisualLabelImpl;
import com.dragome.model.VisualListBoxImpl;
import com.dragome.model.VisualPanelImpl;
import com.dragome.model.VisualRadioButton;
import com.dragome.model.VisualTextFieldImpl;
import com.dragome.model.interfaces.VisualComboBox;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualImage;
import com.dragome.model.interfaces.VisualLink;
import com.dragome.model.interfaces.VisualProgressBar;
import com.dragome.model.interfaces.VisualTextArea;
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
