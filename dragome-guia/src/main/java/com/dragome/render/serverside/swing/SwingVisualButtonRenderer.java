package com.dragome.render.serverside.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.AbstractButton;

import com.dragome.guia.components.interfaces.VisualButton;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.events.listeners.interfaces.ClickListener;
import com.dragome.render.canvas.CanvasImpl;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.html.renderers.Mergeable;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.templates.interfaces.Template;

public class SwingVisualButtonRenderer implements ComponentRenderer<Object, VisualButton>
{
	public Canvas<Object> render(final VisualButton visualButton)
	{
		CanvasImpl<Object> canvasImpl= new CanvasImpl<Object>();
		
		canvasImpl.setContent(new Mergeable<Object>()
		{
			public void mergeWith(Object element)
			{
				AbstractButton abstractButton= (AbstractButton) element;
				abstractButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						visualButton.getListener(ClickListener.class).clickPerformed(visualButton);
					}
				});
			}

			@Override
			public ComponentRenderer<Object, ?> getRenderer()
			{
				// TODO Auto-generated method stub
				return null;
			}
		});
		return canvasImpl;
	}

	@Override
	public boolean matches(Template child)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<Template> findMatchingTemplateFor(Template template)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean matches(Template child, VisualComponent aVisualComponent)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTemplateCompatible(Template template)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
