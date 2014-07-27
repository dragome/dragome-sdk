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

import java.util.ArrayList;
import java.util.List;

import com.dragome.forms.bindings.client.format.Format;
import com.dragome.forms.bindings.client.format.FormatException;
import com.dragome.forms.bindings.client.list.ArrayListModel;
import com.dragome.forms.bindings.client.list.GuardedListModelChangedHandler;
import com.dragome.forms.bindings.client.value.ValueHolder;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.extra.user.client.Command;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.list.ListModel;
import com.dragome.model.interfaces.list.ListModelChangedEvent;
import com.dragome.model.interfaces.list.MutableListModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Aug 5, 2009
 * Time: 1:02:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormattedListFieldModelImpl<T> extends AbstractListFieldModelBase<T> implements FormattedListFieldModel<T>
{
	private ValueHolder<Format<T>> formatModel= new ValueHolder<Format<T>>();
	private ArrayListModel<String> textModel= new ArrayListModel<String>();
	private ListFormatExceptionPolicy<T> formatExceptionPolicy= new ListFormatExceptionPolicy<T>()
	{
		public void onFormatException(String value, List<T> values, FormatException e)
		{
		}
	};

	private GuardedListModelChangedHandler<T> valueMonitor= new GuardedListModelChangedHandler<T>()
	{
		public void onGuardedListDataChanged(ListModelChangedEvent<T> event)
		{
			writeValuesToText();
		}
	};

	private GuardedListModelChangedHandler<String> textMonitor= new GuardedListModelChangedHandler<String>()
	{
		public void onGuardedListDataChanged(ListModelChangedEvent<String> event)
		{
			writeTextToValues();
		}
	};

	private ValueChangeHandler<Format<T>> formatMonitor= new ValueChangeHandler<Format<T>>()
	{
		public void onValueChange(ValueChangeEvent<Format<T>> event)
		{
			writeValuesToText();
		}
	};

	public FormattedListFieldModelImpl(FormModel formModel, ListModel<T> source, Format<T> format, ListFormatExceptionPolicy<T> exceptionPolicy, Class<T> valueType)
	{
		super(formModel, source, valueType);
		setFormat(format);
		setFormatExceptionPolicy(exceptionPolicy);
		addListModelChangedHandler(valueMonitor);
		textModel.addListModelChangedHandler(textMonitor);
		formatModel.addValueChangeHandler(formatMonitor);
	}

	public MutableListModel<String> getTextModel()
	{
		return textModel;
	}

	public void setFormat(Format<T> format)
	{
		if (format == null)
		{
			throw new NullPointerException("format is null");
		}

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
		// we try and sanitise the users text by parsing it and
		// reformatting it.  We don't touch the real value while doing this
		// as if the current text value is invalid we'll actually blat the
		// current value.
		ArrayList<String> sanitisedTextValues= new ArrayList<String>(size());

		for (String text : textModel)
		{
			sanitisedTextValues.add(sanitiseText(text));
		}

		updateTextModel(sanitisedTextValues);
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

	private String sanitiseText(String text)
	{
		try
		{
			Format<T> format= getFormat();
			T value= format.parse(text);
			return format.format(value);
		}
		catch (FormatException e)
		{
			return text;
		}
	}

	public ListFormatExceptionPolicy<T> getFormatExceptionPolicy()
	{
		return formatExceptionPolicy;
	}

	public void setFormatExceptionPolicy(ListFormatExceptionPolicy<T> formatExceptionPolicy)
	{
		if (formatExceptionPolicy == null)
		{
			throw new NullPointerException("formatExceptionPolicy is null");
		}

		this.formatExceptionPolicy= formatExceptionPolicy;
	}

	private void writeTextToValues()
	{
		ArrayList<T> newValues= new ArrayList<T>(textModel.size());

		for (String value : (ListModel<String>) textModel)
		{
			try
			{
				newValues.add(getFormat().parse(value));
			}
			catch (FormatException e)
			{
				formatExceptionPolicy.onFormatException(value, newValues, e);
			}
		}

		updateModelValues(newValues);
	}

	private void writeValuesToText()
	{
		ArrayList<String> newValues= new ArrayList<String>(size());
		for (T value : (ListModel<T>) this)
		{
			newValues.add(getFormat().format(value));
		}
		updateTextModel(newValues);
	}

	private void updateModelValues(ArrayList<T> newValues)
	{
		try
		{
			valueMonitor.setIgnoreEvents(true);
			setElements(newValues);
		}
		finally
		{
			valueMonitor.setIgnoreEvents(false);
		}
	}

	private void updateTextModel(ArrayList<String> newValues)
	{
		try
		{
			textMonitor.setIgnoreEvents(true);
			textModel.setElements(newValues);
		}
		finally
		{
			textMonitor.setIgnoreEvents(false);
		}
	}

}