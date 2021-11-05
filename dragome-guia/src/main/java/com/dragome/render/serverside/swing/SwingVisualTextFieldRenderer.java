package com.dragome.render.serverside.swing;

import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dragome.guia.components.interfaces.VisualTextField;
import com.dragome.render.canvas.CanvasImpl;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.html.renderers.Mergeable;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.templates.interfaces.Template;

public class SwingVisualTextFieldRenderer implements ComponentRenderer<Object, VisualTextField<Object>>
{
	public Canvas<Object> render(final VisualTextField<Object> visualTextField)
	{
		CanvasImpl<Object> canvasImpl= new CanvasImpl<Object>();

		canvasImpl.setContent(new Mergeable<Object>()
		{
			public void mergeWith(Object element)
			{
				final JTextField jTextField= (JTextField) element;

				SwingUtils.addChangeListener(jTextField, new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						visualTextField.setValue(jTextField.getText());
					}
				});

				jTextField.setText(visualTextField.getValue() + "");
			}
		});
		return canvasImpl;
	}

	@Override
	public boolean matches(VisualTextField<Object> aVisualComponent, Template child)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
