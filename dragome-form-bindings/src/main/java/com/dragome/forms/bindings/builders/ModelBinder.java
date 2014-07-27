/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.forms.bindings.builders;

import com.dragome.forms.bindings.client.bean.AbstractBeanModelProvider;
import com.dragome.forms.bindings.client.form.FieldModel;
import com.dragome.forms.bindings.client.form.FormModel;
import com.dragome.forms.bindings.client.form.FormattedFieldModel;
import com.dragome.forms.bindings.client.form.ListFieldModel;
import com.dragome.forms.bindings.client.form.binding.FormBinder;
import com.dragome.forms.bindings.client.format.Format;
import com.dragome.forms.bindings.client.style.StyleBinder;
import com.dragome.forms.bindings.client.value.ValueHolder;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.reflect.ReflectionBeanModelProvider;
import com.dragome.model.interfaces.HasValue;
import com.dragome.model.interfaces.HasVisible;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.model.interfaces.list.HasListModel;

public class ModelBinder<T>
{
	protected AbstractBeanModelProvider<T> modelProvider;
	protected FormModel formModel= new FormModel();
	protected FormBinder binder= new FormBinder();
	private VisualPanel panel;

	public ModelBinder(T model)
	{
		modelProvider= new ReflectionBeanModelProvider<T>((Class<T>) model.getClass());
		modelProvider.setBeanSource(new ValueHolder<T>(model));
		modelProvider.setAutoCommit(true);
	}

	public ModelBinder(T model, VisualPanel panel)
	{
		this(model);
		this.panel= panel;
	}

	public <V extends VisualComponent & HasValue<String>> ModelBinder<T> bind(String key, V aHasValue)
	{
		bindMemberToValueHolder(key, aHasValue, String.class);
		//		System.out.println(fieldModel.getValue());
		return this;
	}

	public <V extends VisualComponent & HasValue<H>, H> void bindFormattedMemberToValueHolder(String propertyName, V aHasValue, Class<? extends H> type, Format formatter)
	{
		FormattedFieldModel<? extends H> formattedFieldModel= formModel.formattedFieldOfType(type).using(formatter).boundTo(modelProvider, propertyName);
		aHasValue.setValue(formattedFieldModel.getValue());
		binder.bind(formattedFieldModel).to((HasValue<String>) aHasValue);
	}

	public <V extends VisualComponent & HasValue<H>, H> FieldModel<H> bindMemberToValueHolder(String propertyName, V aHasValue, Class<? extends H> type)
	{
		FieldModel<H> fieldModel= (FieldModel<H>) formModel.fieldOfType(type).boundTo(modelProvider, propertyName);
		aHasValue.setValue(fieldModel.getValue());
		binder.bind(fieldModel).to(aHasValue);
		return fieldModel;
	}

	public <V extends VisualComponent & HasValue<String>> ModelBinder<T> bindToPanel(String key, V aHasValue)
	{
		panel.addChild(aHasValue);
		return bind(key, aHasValue);
	}

	public <V extends VisualComponent & HasValue<String>> ModelBinder<T> bindToPanel(V component)
	{
		return bindToPanel(component.getName(), component);
	}

	public <V extends HasValue<String>> ModelBinder<T> bindToPanel(String key, V aHasValue)
	{
		return bindToPanel(key, aHasValue);
	}

	public <V, H> void bindListMemberToHasListModel(String propertyName, HasListModel<H> hasListModel, Class<? extends H> type)
	{
		ListFieldModel<H> listFieldModel= (ListFieldModel<H>) new FormModel().listOfType(type).boundTo(modelProvider, propertyName);
		binder.bind(listFieldModel).to(hasListModel.getListModel());
	}

	public ModelBinder<T> bindVisible(HasVisible hasVisible, ValueModel<Boolean> valueModel)
	{
		binder.show(hasVisible).when(valueModel);
		return this;
	}

	public ModelBinder<T> bindStyle(VisualComponent visualComponent, ValueModel<Boolean> valueModel)
	{
		StyleBinder style= new StyleBinder();
		style.style(visualComponent).with("disabled").when(valueModel);
		return this;
	}

	public <V extends VisualComponent & HasValue<H>, H> FieldModel<H> bindMemberToValueHolderAdding(String propertyName, V aHasValue, Class<? extends H> type)
	{
		FieldModel<H> result= bindMemberToValueHolder(propertyName, aHasValue, type);
		panel.addChild(aHasValue);
		return result;
	}

}
