package com.dragome.render.serverside.swing;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.dragome.guia.components.interfaces.VisualButton;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualLabel;
import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.guia.components.interfaces.VisualTextField;
import com.dragome.render.canvas.CanvasImpl;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.canvas.interfaces.CanvasFactory;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateLoadingStrategy;
import com.dragome.templates.interfaces.TemplateManager;

public class SwingTemplateManager implements TemplateManager
{
	private static Map<Class<? extends VisualComponent>, Class<? extends ComponentRenderer<Object, ? extends VisualComponent>>> renderers= new HashMap<Class<? extends VisualComponent>, Class<? extends ComponentRenderer<Object, ? extends VisualComponent>>>();

	public TemplateHandler getTemplateHandler()
	{
		return new SwingTemplateHandler();
	}

	public TemplateLoadingStrategy getTemplateHandlingStrategy()
	{
		return new SwingTemplateLoadingStrategy();
	}

	public CanvasFactory getCanvasFactory()
	{
		return null;
	}

	public Template createTemplate(String aTemplateName)
	{
		return null;
	}

	public ComponentRenderer getComponentRenderer()
	{
		return new ComponentRenderer<Object, Object>()
		{
			public Canvas<Object> render(final Object aVisualComponent)
			{
				ComponentRenderer renderer= null;
				if (aVisualComponent instanceof VisualButton)
					renderer= new SwingVisualButtonRenderer();
				else if (aVisualComponent instanceof VisualTextField)
					renderer= new SwingVisualTextFieldRenderer();
				else if (aVisualComponent instanceof VisualLabel)
					renderer= new SwingVisualLabelRenderer();
				else if (aVisualComponent instanceof VisualPanel)
					renderer= new SwingVisualPanelRenderer();

				for (Entry<Class<? extends VisualComponent>, Class<? extends ComponentRenderer<Object, ? extends VisualComponent>>> entry : renderers.entrySet())
				{
					if (entry.getKey().isAssignableFrom(aVisualComponent.getClass()))
						renderer= ServiceLocator.getInstance().getReflectionService().createClassInstance(entry.getValue());
				}

				if (renderer != null)
					return renderer.render(aVisualComponent);
				else
					return new CanvasImpl<>();
			}
		};
	}
	
	public static <A extends VisualComponent> void addRenderFor(Class<A> componentClass, Class<? extends ComponentRenderer<Object, ? extends A>> rendererClass)
	{
		renderers.put(componentClass, rendererClass);
	}
}
