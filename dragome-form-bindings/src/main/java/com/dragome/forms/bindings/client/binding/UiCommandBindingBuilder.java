package com.dragome.forms.bindings.client.binding;

import com.dragome.forms.bindings.client.command.UiCommand;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.model.interfaces.ClickEvent;
import com.dragome.model.interfaces.ClickHandler;
import com.dragome.model.interfaces.HandlerRegistration;
import com.dragome.model.interfaces.HasClickHandlers;
import com.dragome.model.interfaces.HasEnabled;
import com.dragome.model.pectin.HasClickHandlersAdapter;

/**
* Created by IntelliJ IDEA.
* User: andrew
* Date: Mar 25, 2010
* Time: 12:17:35 PM
* To change this template use File | Settings | File Templates.
*/
public class UiCommandBindingBuilder
{
	private UiCommand uiCommand;
	private Binder container;

	public UiCommandBindingBuilder(Binder container, UiCommand uiCommand)
	{
		this.container= container;
		this.uiCommand= uiCommand;
	}

	public void to(final HasClickHandlers button)
	{
		HandlerRegistration registration= button.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				uiCommand.execute();
			}
		});

		container.registerDisposable(registration);

		bindEnabled(uiCommand, button);
	}

	private void bindEnabled(UiCommand uiCommand, HasClickHandlers button)
	{
		/*if (button instanceof FocusWidget)
		{
		   container.enable((FocusWidget) button).when(uiCommand.enabled());
		}
		else*/if (button instanceof HasEnabled)
		{
			container.enable((HasEnabled) button).when(uiCommand.enabled());
		}
		else
		{
			throw new IllegalStateException("Button doesn't extend FocusWidget or implement HasEnabled");
		}
	}

	public void to(VisualComponent visualComponent)
	{
		to(new HasClickHandlersAdapter(visualComponent));
	}
}
