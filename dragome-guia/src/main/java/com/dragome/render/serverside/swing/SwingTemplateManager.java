package com.dragome.render.serverside.swing;

import com.dragome.guia.components.interfaces.VisualButton;
import com.dragome.guia.components.interfaces.VisualLabel;
import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.guia.components.interfaces.VisualTextField;
import com.dragome.render.canvas.CanvasImpl;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.canvas.interfaces.CanvasFactory;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.templates.interfaces.Template;
import com.dragome.templates.interfaces.TemplateLoadingStrategy;
import com.dragome.templates.interfaces.TemplateManager;

public class SwingTemplateManager implements TemplateManager
{
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
				if (aVisualComponent instanceof VisualButton)
					return new SwingVisualButtonRenderer().render((VisualButton) aVisualComponent);
				else if (aVisualComponent instanceof VisualTextField)
					return new SwingVisualTextFieldRenderer().render((VisualTextField) aVisualComponent);
				else if (aVisualComponent instanceof VisualLabel)
					return new SwingVisualLabelRenderer().render((VisualLabel) aVisualComponent);
				else if (aVisualComponent instanceof VisualPanel)
					return new SwingVisualPanelRenderer().render((VisualPanel) aVisualComponent);
				else
					return new CanvasImpl<>();
			}

			@Override
			public boolean matches(Object aVisualComponent, Template child)
			{
				// TODO Auto-generated method stub
				return false;
			}
		};
	}
}
