package com.dragome.forms.bindings.client.form.binding;

import com.dragome.forms.bindings.client.binding.AbstractBinding;
import com.dragome.forms.bindings.client.form.FormattedFieldBase;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 26, 2010
 * Time: 9:30:30 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFormattedBinding<M extends FormattedFieldBase> extends AbstractBinding
{
	private M model;

	protected AbstractFormattedBinding(M model)
	{
		this.model= model;
	}

	public M getModel()
	{
		return model;
	}

	//   protected void sanitiseTextOnBlur(HasBlurHandlers widget)
	//   {
	//      // We'll update the widget on focus lost in case the format version of the
	//      // value is different from what was typed.. i.e if text->parse->value->format->text
	//      // produces a different text value.
	//      //
	//      // We don't try and do this during value change events since if the widget fires
	//      // value change event on key events the users typing will be clobbered.
	//      registerDisposable(widget.addBlurHandler(new BlurMonitor()));
	//   }
	//
	//   private class BlurMonitor implements BlurHandler
	//   {
	//      public void onBlur(BlurEvent event)
	//      {
	//         model.sanitiseText();
	//      }
	//   }
}
