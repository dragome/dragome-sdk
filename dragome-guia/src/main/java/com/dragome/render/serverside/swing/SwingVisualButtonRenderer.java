package com.dragome.render.serverside.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

import com.dragome.guia.components.interfaces.VisualButton;
import com.dragome.guia.listeners.ClickListener;
import com.dragome.render.canvas.CanvasImpl;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.html.components.Mergeable;
import com.dragome.render.interfaces.ComponentRenderer;

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
		});
		return canvasImpl;
	}
}
