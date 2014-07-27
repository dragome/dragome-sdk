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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.dragome.forms.bindings.client.format.Format;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.list.ListModel;

/**
 * <p>
 * FormModel provides builders for creating {@link FieldModel}s, {@link FormattedFieldModel}s
 * and {@link ListFieldModel}.  Widgets can then be bound to these models using a {@link com.pietschy.gwt.pectin.client.form.binding.FormBinder}.
 * </p>
 * <p>
 * FormModels also allow support properties to allow plugins to store additional
 * information and {@link BindingCallback}s to hook into the binding process.
 * </p>
 *
 * @see #fieldOfType(Class)
 * @see #formattedFieldOfType(Class)
 * @see #listOfType(Class)
 */
public class FormModel
{
	private HashMap<Object, Object> properties= new HashMap<Object, Object>();
	private ArrayList<BindingCallback> bindingCallbacks= new ArrayList<BindingCallback>();

	private ArrayList<Field<?>> allFields= new ArrayList<Field<?>>();

	/**
	 * Returns a {@link FieldModel} buider of the specified type.
	 *
	 * @param type the value type held by the field.
	 * @return a builder for the specified type.
	 */
	public <T> FieldBuilder<T> fieldOfType(Class<T> type)
	{
		return new FieldBuilder<T>(this, type);
	}

	/**
	 * Returns a {@link FormattedFieldModel} builder for the specified type.
	 *
	 * @param type the value type held by the field.
	 * @return a builder for the specified type.
	 */
	public <T> FormattedFieldFormatBuilder<T> formattedFieldOfType(Class<T> type)
	{
		return new FormattedFieldFormatBuilder<T>(this, type);
	}

	/**
	 * Returns a {@link ListFieldModel} builder for the specified type.
	 *
	 * @param type the value type held by the list.
	 * @return a builder for the specified type.
	 */
	public <T> ListFieldBindingBuilder<T> listOfType(Class<T> type)
	{
		return new ListFieldBindingBuilder<T>(this, type);
	}

	/**
	 * Returns a {@link FormattedListFieldModel} builder for the specified type.
	 *
	 * @param type the value type held by the list.
	 * @return a builder for the specified type.
	 */
	public <T> FormattedListFieldFormatBuilder<T> formattedListOfType(Class<T> type)
	{
		return new FormattedListFieldFormatBuilder<T>(this, type);
	}

	/**
	 * Returns all the fields in this form model at the time this method is called.  Fields added
	 * after this method is invoked will not be included in the collection.
	 * <p/>
	 * Fields are in the order they are added to the form.
	 *
	 * @return a collection of the fields in this model.
	 */
	public Collection<Field<?>> allFields()
	{
		// It may be tempting to include a live collection of the fields so that the notion of
		// "all fields" is true no matter when the method was used to construct an expression
		// This however would cause problems as most bindings take some initialisation action
		// when a new field is added.
		//
		// Thus if we want this method to return a live collection then we will need to:
		// a) make the collection observable (i.e. make it a ListModel)
		// b) ensure all bindings that take a collection of fields observe the collection and
		//    ensure new additions to it are initialised appropriately.
		//
		// but for now we return a copy.
		return new ArrayList<Field<?>>(allFields);
	}

	/**
	 * Returns all the fields in this form model at the time this method is called except those
	 * specified.  Fields added after this method is invoked will not be included in the collection.
	 * <p/>
	 * Fields are in the order they are added to the form.
	 *
	 * @return a collection of the fields in this model.
	 */
	public Collection<Field<?>> allFieldsExcept(Field<?> first, Field<?>... others)
	{
		// See the notes in allFields() for details on why I haven't got around to
		// creating a live collections.
		ArrayList<Field<?>> list= new ArrayList<Field<?>>(allFields);
		list.remove(first);
		for (Field<?> other : others)
		{
			list.remove(other);
		}
		return list;
	}

	/**
	 * Factory method for creating field model instances.  This method is invoked by the field model
	 * builder and is provided so subclasses can override the specific type that is returned.
	 *
	 * @param model     the source value model.
	 * @param valueType the type of the value held by this model.
	 * @return a new field model
	 */
	protected <T> FieldModel<T> createFieldModel(ValueModel<T> model, Class<T> valueType)
	{
		FieldModelImpl<T> field= new FieldModelImpl<T>(this, model, valueType);
		allFields.add(field);
		return field;
	}

	/**
	 * Factory method for creating field model instances.  This method is invoked by the field model
	 * builder and is provided so subclasses can override the specific type that is returned.
	 *
	 * @param source          the source value model.
	 * @param format          the format to use
	 * @param exceptionPolicy the policy for handling format exceptions.
	 * @param valueType       the type of the value held by this model.
	 * @return a new field model
	 */
	protected <T> FormattedFieldModel<T> createFormattedFieldModel(ValueModel<T> source, Format<T> format, FormatExceptionPolicy<T> exceptionPolicy, Class<T> valueType)
	{
		FormattedFieldModelImpl<T> field= new FormattedFieldModelImpl<T>(this, source, format, exceptionPolicy, valueType);
		allFields.add(field);
		return field;
	}

	/**
	 * Factory method for creating field model instances.  This method is invoked by the field model
	 * builder and is provided so subclasses can override the specific type that is returned.
	 *
	 * @param source    the source value model.
	 * @param valueType the type of the value held by this model.
	 * @return a new field model
	 */
	protected <T> ListFieldModel<T> createListModel(ListModel<T> source, Class<T> valueType)
	{
		ListFieldModelImpl<T> field= new ListFieldModelImpl<T>(this, source, valueType);
		allFields.add(field);
		return field;
	}

	/**
	 * Factory method for creating field model instances.  This method is invoked by the field model
	 * builder and is provided so subclasses can override the specific type that is returned.
	 *
	 * @param source          the source value model.
	 * @param format          the format to use
	 * @param exceptionPolicy the policy for handling format exceptions.
	 * @param valueType       the type of the value held by this model.
	 * @return a new field model
	 */
	public <T> FormattedListFieldModel<T> createFormattedListFieldModel(ListModel<T> source, Format<T> format, ListFormatExceptionPolicy<T> exceptionPolicy, Class<T> valueType)
	{
		return new FormattedListFieldModelImpl<T>(this, source, format, exceptionPolicy, valueType);
	}

	public void putProperty(Object key, Object value)
	{
		properties.put(key, value);
	}

	public Object getProperty(Object key)
	{
		return properties.get(key);
	}

	public void addBindingCallback(BindingCallback callback)
	{
		bindingCallbacks.add(callback);
	}

	public Collection<BindingCallback> getBindingCallbacks()
	{
		return Collections.unmodifiableCollection(bindingCallbacks);
	}

}
