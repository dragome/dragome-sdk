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

import java.util.List;

import com.dragome.debugging.execution.DragomeVisualActivity;
import com.dragome.forms.bindings.client.form.binding.FormBinder;
import com.dragome.forms.bindings.client.value.ValueSource;
import com.dragome.model.VisualButtonImpl;
import com.dragome.model.VisualCheckboxImpl;
import com.dragome.model.VisualLabelImpl;
import com.dragome.model.VisualLinkImpl;
import com.dragome.model.VisualPanelImpl;
import com.dragome.model.VisualRadioButton;
import com.dragome.model.VisualRadioButtonImpl;
import com.dragome.model.VisualTextFieldImpl;
import com.dragome.model.interfaces.HasValue;
import com.dragome.model.interfaces.VisualButton;
import com.dragome.model.interfaces.VisualCheckbox;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualLabel;
import com.dragome.model.interfaces.VisualLink;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.model.interfaces.VisualTextField;
import com.dragome.model.listeners.BlurListener;
import com.dragome.model.listeners.ClickListener;
import com.dragome.model.listeners.DoubleClickListener;
import com.dragome.model.listeners.InputListener;
import com.dragome.model.listeners.KeyUpListener;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Template;

public class TemplateComponentBindingBuilder<T, C extends VisualComponent>
{
    private C component;
    private Class<? extends VisualComponent> componentType;
    private T model;
    private VisualPanel panel;
    private FormBinder binder= new FormBinder();
    private Template template;

    public TemplateComponentBindingBuilder(Template template, VisualPanel panel, T model, Class<C> componentType)
    {
	this.panel= panel;
	this.model= model;
	this.template= template;
	if (componentType.equals(VisualPanel.class))
	    this.componentType= VisualPanelImpl.class;
	else if (componentType.equals(VisualTextField.class))
	    this.componentType= VisualTextFieldImpl.class;
	else if (componentType.equals(VisualButton.class))
	    this.componentType= VisualButtonImpl.class;
	else if (componentType.equals(VisualCheckbox.class))
	    this.componentType= VisualCheckboxImpl.class;
	else if (componentType.equals(VisualRadioButton.class))
	    this.componentType= VisualRadioButtonImpl.class;
	else if (componentType.equals(VisualLink.class))
	    this.componentType= VisualLinkImpl.class;
	else if (componentType.equals(VisualLabel.class))
	    this.componentType= VisualLabelImpl.class;

	component= (C) ServiceLocator.getInstance().getReflectionService().createClassInstance(this.componentType);
	setupComponent();
    }

    private void setupComponent()
    {
	component.setName(template.getName());
	if (component instanceof VisualPanel)
	    ((VisualPanel) component).initLayout(new TemplateLayout(template));
    }

    public TemplateComponentBindingBuilder(Template template, VisualPanel panel, T model, C component)
    {
	this.template= template;
	this.panel= panel;
	this.model= model;
	this.component= component;
	setupComponent();
    }

    public ComponentBuilder<T> childBuilder()
    {
	build();
	return new ComponentBuilder<T>((VisualPanel) component, model);
    }

    public <S> RepeaterBuilder<S, T> toListProperty(final Getter<T, List<S>> getter)
    {
	final ValueModelDelegator<T, List<S>> valueModelDelegator= new ValueModelDelegator<T, List<S>>(model);

	List<S> list= getter.get(model);
	
	addListenerIfObservable(valueModelDelegator, list);
	
	NullMutableValueModel<List<S>> valueSource= new NullMutableValueModel<List<S>>()
	{
	    public List<S> getDelegatedValue()
	    {
		return getter.get(model);
	    }
	};
	
	valueModelDelegator.setValueSource(valueSource);	
	
	BindingSync.addCondition(valueModelDelegator);
	return new RepeaterBuilder<S, T>(valueModelDelegator, template, model, panel);
    }

    private <S> void addListenerIfObservable(final ValueModelDelegator<T, List<S>> valueModelDelegator, List<S> list)
    {
	if (list instanceof ObservableList)
	{
	    ObservableList<S> observableList= (ObservableList<S>) list;
	    observableList.setListChangeListener(new ListChangedListener()
	    {
		public void listChanged()
		{
		    valueModelDelegator.fireValueChangeEvent();
		}
	    });
	}
    }

    public <S> RepeaterBuilder<S, T> toList(final List<S> list)
    {
	final ValueModelDelegator<T, List<S>> valueModelDelegator= new ValueModelDelegator<T, List<S>>(model);
	addListenerIfObservable(valueModelDelegator, list);

	NullMutableValueModel<List<S>> valueSource= new NullMutableValueModel<List<S>>()
	{
	    public List<S> getDelegatedValue()
	    {
		return list;
	    }
	};

	valueModelDelegator.setValueSource(valueSource);

	BindingSync.addCondition(valueModelDelegator);
	return new RepeaterBuilder<S, T>(valueModelDelegator, template, model, panel);
    }

    public TemplateComponentBindingBuilder<T, C> onDoubleClick(DoubleClickListener doubleClickPerformed)
    {
	component.addDoubleClickListener(doubleClickPerformed);
	return this;
    }

    public TemplateComponentBindingBuilder<T, C> onClick(ClickListener clickListener)
    {
	component.addClickListener(clickListener);
	return this;
    }

    public TemplateComponentBindingBuilder<T, C> onClick(final ActionExecutor actionExecutor)
    {
	component.addClickListener(new ClickListener()
	{
	    public void clickPerformed(VisualComponent aVisualComponent)
	    {
		actionExecutor.execute();
	    }
	});
	return this;
    }

    public VisualComponent component()
    {
	return component;
    }

    public TemplateComponentBindingBuilder<T, C> onKeyUp(final KeyUpListener keyUpListener, final int... codes)
    {
	component.addKeyListener(new KeyUpListener()
	{
	    public void keyupPerformed(VisualComponent visualComponent, int keyCode)
	    {
		for (int code : codes)
		{
		    if (code == keyCode || code == 0)
		    {
			keyUpListener.keyupPerformed(visualComponent, keyCode);
			break;
		    }
		}
	    }
	});
	return this;
    }

    public TemplateComponentBindingBuilder<T, C> onBlur(BlurListener blurListener)
    {
	component.addListener(BlurListener.class, blurListener);
	return this;
    }

    public <S> TemplateComponentBindingBuilder<T, C> toProperty(final Getter<T, S> getter, final Setter<T, S> setter)
    {
	ValueModelDelegator<T, S> valueModelDelegator= new ValueModelDelegator<T, S>(new NullMutableValueModel<S>()
	{
	    public S getDelegatedValue()
	    {
		return getter.get(model);
	    }

	    public void setValue(S value)
	    {
		setter.set(model, value);
	    }
	}, model);

	return with(valueModelDelegator);
    }

    public <S> TemplateComponentBindingBuilder<T, C> toProperty(String propertyName)
    {
	return with(new ValueModelDelegator<T, S>(new NullMutableValueModel<S>(), model));
    }

    private <S> TemplateComponentBindingBuilder<T, C> with(ValueModelDelegator<T, S> valueModelDelegator)
    {
	binder.bind(valueModelDelegator).to((HasValue<S>) component);
	BindingSync.addCondition(valueModelDelegator);
	return this;
    }

    public <S> TemplateComponentBindingBuilder<T, C> to(final ValueSource<S> valueSource)
    {
	ValueModelDelegator<T, S> valueModelDelegator= new ValueModelDelegator<T, S>(new NullMutableValueModel<S>()
	{
	    public S getDelegatedValue()
	    {
		return valueSource.getValue();
	    }
	}, model);

	return with(valueModelDelegator);
    }

    public C build()
    {
	panel.addChild(component);
	return component;
    }

    public TemplateComponentBindingBuilder<T, C> onInput(InputListener inputListener)
    {
	component.addListener(InputListener.class, inputListener);
	return this;
    }

}
