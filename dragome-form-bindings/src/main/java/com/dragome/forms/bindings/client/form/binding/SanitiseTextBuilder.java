package com.dragome.forms.bindings.client.form.binding;

//import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.dragome.model.interfaces.HasValue;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 27, 2010
 * Time: 1:26:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class SanitiseTextBuilder
{
	private AbstractFormattedBinding<?> binding;
	private HasValue<?> widget;

	public SanitiseTextBuilder(AbstractFormattedBinding<?> binding, HasValue<?> widget)
	{
		this.binding= binding;
		this.widget= widget;
	}

	//   public void sanitiseTextOnBlur()
	//   {
	//      if (widget instanceof HasBlurHandlers)
	//      {
	//         sanitiseTextOnBlurOf((HasBlurHandlers) widget);
	//      }
	//      else
	//      {
	//         throw new IllegalStateException("Bound widget doesn't implement HasBlurHandlers.");
	//      }
	//   }
	//
	//   public void sanitiseTextOnBlurOf(HasBlurHandlers widget)
	//   {
	//      binding.sanitiseTextOnBlur((HasBlurHandlers) widget);
	//   }
}
