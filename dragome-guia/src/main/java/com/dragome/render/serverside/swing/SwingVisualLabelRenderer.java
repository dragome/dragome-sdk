package com.dragome.render.serverside.swing;

import java.util.Optional;

import javax.swing.JLabel;

import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualLabel;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.render.canvas.CanvasImpl;
import com.dragome.render.canvas.interfaces.Canvas;
import com.dragome.render.html.renderers.Mergeable;
import com.dragome.render.interfaces.ComponentRenderer;
import com.dragome.templates.interfaces.Template;

public class SwingVisualLabelRenderer implements ComponentRenderer<Object, VisualLabel<Object>>
{
	public Canvas<Object> render(final VisualLabel<Object> visualLabel)
	{
		CanvasImpl<Object> canvasImpl= new CanvasImpl<Object>();

		canvasImpl.setContent(new Mergeable<Object>()
		{
			public void mergeWith(Object element)
			{
				final JLabel jLabel= (JLabel) element;

				visualLabel.addValueChangeHandler(new ValueChangeHandler<Object>()
				{
					public void onValueChange(ValueChangeEvent<Object> event)
					{
						jLabel.setText(event.getValue() + "");
					}
				});
				
				jLabel.setText(visualLabel.getValue() + "");
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
