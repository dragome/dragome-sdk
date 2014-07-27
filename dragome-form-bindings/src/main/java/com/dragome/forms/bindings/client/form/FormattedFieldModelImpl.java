/*
 * Copyright 2009 Andrew Pietsch 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 *      
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing permissions 
 * and limitations under the License. 
 */

package com.dragome.forms.bindings.client.form;

import com.dragome.forms.bindings.client.format.Format;
import com.dragome.forms.bindings.client.format.FormatException;
import com.dragome.forms.bindings.client.value.GuardedValueChangeHandler;
import com.dragome.forms.bindings.client.value.MutableValueModel;
import com.dragome.forms.bindings.client.value.ValueHolder;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.extra.user.client.Command;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Aug 5, 2009
 * Time: 1:02:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormattedFieldModelImpl<T> extends AbstractFieldModelBase<T> implements FormattedFieldModel<T>
{
	private ValueHolder<Format<T>> formatModel= new ValueHolder<Format<T>>();
	private MutableValueModel<String> textModel= new ValueHolder<String>();
	private FormatExceptionPolicy<T> formatExceptionPolicy;

	private GuardedValueChangeHandler<T> valueMonitor= new GuardedValueChangeHandler<T>()
	{
		public void onGuardedValueChanged(ValueChangeEvent<T> event)
		{
			writeValueToText(event.getValue());
		}
	};

	private GuardedValueChangeHandler<String> textMonitor= new GuardedValueChangeHandler<String>()
	{
		public void onGuardedValueChanged(ValueChangeEvent<String> event)
		{
			writeTextToValue(event.getValue());
		}
	};

	private ValueChangeHandler<Format<T>> formatMonitor= new ValueChangeHandler<Format<T>>()
	{
		public void onValueChange(ValueChangeEvent<Format<T>> event)
		{
			writeValueToText(getValue());
		}
	};

	public FormattedFieldModelImpl(FormModel formModel, ValueModel<T> source, Format<T> format, FormatExceptionPolicy<T> exceptionPolicy, Class valueType)
	{
		super(formModel, source, valueType);
		setFormat(format);
		setFormatExceptionPolicy(exceptionPolicy);
		addValueChangeHandler(valueMonitor);
		textModel.addValueChangeHandler(textMonitor);
		formatModel.addValueChangeHandler(formatMonitor);
	}

	public MutableValueModel<String> getTextModel()
	{
		return textModel;
	}

	public void setFormat(Format<T> format)
	{
		if (format == null)
		{
			throw new NullPointerException("format is null");
		}

		// this will trigger a writeToText causing the text model to update
		// using the new format.
		this.formatModel.setValue(format);
	}

	public Format<T> getFormat()
	{
		return formatModel.getValue();
	}

	public ValueModel<Format<T>> getFormatModel()
	{
		return formatModel;
	}

	public void sanitiseText()
	{
		try
		{
			// we try and sanitise the users text by parsing it and
			// reformating it.  We don't touch the real value while doing this
			// as if the current text value is invalid we'll actually blat the
			// current value.
			T value= getFormat().parse(textModel.getValue());
			writeValueToText(value);
		}
		catch (FormatException e)
		{
			// ignore
		}
	}

	public Command sanitiseTextCommand()
	{
		return new Command()
		{
			public void execute()
			{
				sanitiseText();
			}
		};
	}

	public void setFormatExceptionPolicy(FormatExceptionPolicy<T> formatExceptionPolicy)
	{
		if (formatExceptionPolicy == null)
		{
			throw new NullPointerException("formatExceptionPolicy is null");
		}

		this.formatExceptionPolicy= formatExceptionPolicy;
	}

	public FormatExceptionPolicy<T> getFormatExceptionPolicy()
	{
		return formatExceptionPolicy;
	}

	protected void writeValueToText(T value)
	{
		try
		{
			textMonitor.setIgnoreEvents(true);
			textModel.setValue(getFormat().format(value));
		}
		finally
		{
			textMonitor.setIgnoreEvents(false);
		}
	}

	protected void writeTextToValue(String value)
	{
		try
		{
			valueMonitor.setIgnoreEvents(true);
			try
			{
				setValue(getFormat().parse(value));
			}
			catch (FormatException e)
			{
				formatExceptionPolicy.onFormatException(this, e);
			}
		}
		finally
		{
			valueMonitor.setIgnoreEvents(false);
		}
	}

}
