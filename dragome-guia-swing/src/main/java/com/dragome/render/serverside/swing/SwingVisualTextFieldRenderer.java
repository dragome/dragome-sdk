package com.dragome.render.serverside.swing;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import com.dragome.guia.components.interfaces.VisualTextField;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.render.canvas.CanvasImpl;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.html.renderers.Mergeable;
import com.dragome.render.interfaces.ComponentRenderer;

public class SwingVisualTextFieldRenderer implements ComponentRenderer<Object, VisualTextField<Object>>
{
	public Canvas<Object> render(final VisualTextField<Object> visualTextField)
	{
		CanvasImpl<Object> canvasImpl= new CanvasImpl<Object>();

		canvasImpl.setContent(new Mergeable<Object>()
		{
			public void mergeWith(Object element)
			{
				final JTextComponent jTextField= (JTextComponent) element;

				SwingUtils.addChangeListener(jTextField, new ChangeListener()
				{
					public void stateChanged(ChangeEvent e)
					{
						visualTextField.setValue(jTextField.getText());
					}
				});

				jTextField.setText(visualTextField.getValue() + "");

				visualTextField.addValueChangeHandler(new ValueChangeHandler<Object>()
				{
					public void onValueChange(ValueChangeEvent<Object> event)
					{
						String value= visualTextField.getRenderer().render(event.getValue());
						jTextField.setText(value);
					}
				});

			}
		});
		return canvasImpl;
	}
}
