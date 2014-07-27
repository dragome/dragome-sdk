package com.dragome.forms.bindings.client.form;

import com.dragome.forms.bindings.client.format.Format;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.extra.user.client.Command;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Nov 21, 2009
 * Time: 10:35:08 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FormattedFieldBase<T>
{
	Format<T> getFormat();

	void setFormat(Format<T> format);

	ValueModel<Format<T>> getFormatModel();

	/**
	 * Forces the text value to be sanitised according to the current format.  This method will
	 * has a very strict as follows:
	 * <ol>
	 * <li>This method will not affect the current value of the model in any way.</li>
	 * <li>Text values that throw FormatException are left as is.</li>
	 * <li>No value change events are fired</li>
	 * </ol>
	 *
	 *
	 * This is useful if the user entered text that would otherwise be formatted differently by the format.
	 * For example the user may enter 12 and the format might display this as $12.00.
	 */
	public void sanitiseText();

	/**
	 * Returns a new command instance that will invoke {@link #sanitiseText()}.
	 * @return a new command instance that will invoke {@link #sanitiseText()}.
	 */
	public Command sanitiseTextCommand();
}
