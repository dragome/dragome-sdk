package com.dragome.render.serverside.swing;

import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.render.canvas.CanvasImpl;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.html.renderers.Mergeable;
import com.dragome.render.interfaces.ComponentRenderer;

public class SwingVisualPanelRenderer implements ComponentRenderer<Object, VisualPanel>
{
	public Canvas<Object> render(final VisualPanel visualPanel)
	{
		CanvasImpl<Object> canvasImpl= new CanvasImpl<Object>();
		
		canvasImpl.setContent(new Mergeable<Object>()
		{
			public void mergeWith(Object element)
			{
			}
		});
		return canvasImpl;
	}
}
