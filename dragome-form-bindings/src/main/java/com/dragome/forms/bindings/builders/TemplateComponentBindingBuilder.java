/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.forms.bindings.builders;

import java.util.List;

import com.dragome.forms.bindings.client.form.binding.FormBinder;
import com.dragome.forms.bindings.client.value.ValueSource;
import com.dragome.guia.components.VisualButtonImpl;
import com.dragome.guia.components.VisualCheckboxImpl;
import com.dragome.guia.components.VisualImageImpl;
import com.dragome.guia.components.VisualLabelImpl;
import com.dragome.guia.components.VisualLinkImpl;
import com.dragome.guia.components.VisualPanelImpl;
import com.dragome.guia.components.VisualRadioButton;
import com.dragome.guia.components.VisualRadioButtonImpl;
import com.dragome.guia.components.VisualTextFieldImpl;
import com.dragome.guia.components.interfaces.VisualButton;
import com.dragome.guia.components.interfaces.VisualCheckbox;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualImage;
import com.dragome.guia.components.interfaces.VisualLabel;
import com.dragome.guia.components.interfaces.VisualLink;
import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.guia.components.interfaces.VisualTextField;
import com.dragome.model.interfaces.HasValue;
import com.dragome.services.ServiceLocator;
import com.dragome.templates.TemplateLayout;
import com.dragome.templates.interfaces.Template;

public class TemplateComponentBindingBuilder<C extends VisualComponent> extends BaseBuilder<C, TemplateComponentBindingBuilder<C>>
{
	private Class<? extends VisualComponent> componentType;
	private VisualPanel panel;
	private FormBinder binder= new FormBinder();
	private Template template;
	private boolean built= false;

	public TemplateComponentBindingBuilder(Template template, VisualPanel panel, Class<C> componentType, BaseBuilder<? extends VisualComponent, ?> parentBuilder)
	{
		setParentBuilder((BaseBuilder<? extends VisualComponent, TemplateComponentBindingBuilder<C>>) parentBuilder);
		this.panel= panel;
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
		else if (componentType.equals(VisualImage.class))
			this.componentType= VisualImageImpl.class;

		component= (C) ServiceLocator.getInstance().getReflectionService().createClassInstance(this.componentType);
		setupComponent();
	}

	private void setupComponent()
	{
		component.setName(template.getName());
		if (component instanceof VisualPanel)
		{
			VisualPanel visualPanel= (VisualPanel) component;
			if (!(visualPanel.getLayout() instanceof TemplateLayout) || ((TemplateLayout) visualPanel.getLayout()).getTemplate() == null)
				visualPanel.initLayout(new TemplateLayout(template));
		}
	}

	public TemplateComponentBindingBuilder(Template template, VisualPanel panel, C component, BaseBuilder<? extends VisualComponent, ?> parentBuilder)
	{
		setParentBuilder((BaseBuilder<? extends VisualComponent, TemplateComponentBindingBuilder<C>>) parentBuilder);
		this.template= template;
		this.panel= panel;
		this.component= component;
		setupComponent();
	}

	//    private ComponentBuilder childBuilder()
	//    {
	//	build();
	//	return childrenBuilder();
	//    }

	public <S> RepeaterBuilder<S> toListProperty(final Supplier<List<S>> getter)
	{
		final ValueModelDelegator<List<S>> valueModelDelegator= new ValueModelDelegator<List<S>>();

		List<S> list= getter.get();

		addListenerIfObservable(valueModelDelegator, list);

		NullMutableValueModel<List<S>> valueSource= new NullMutableValueModel<List<S>>()
		{
			public List<S> getDelegatedValue()
			{
				return getter.get();
			}
		};

		valueModelDelegator.setValueSource(valueSource);

		BindingSync.addCondition(valueModelDelegator);
		return new RepeaterBuilder<S>(valueModelDelegator, template, panel, (TemplateComponentBindingBuilder<VisualPanel>) this);
	}

	private <S> void addListenerIfObservable(final ValueModelDelegator<List<S>> valueModelDelegator, List<S> list)
	{
		if (list instanceof ObservableList)
		{
			ObservableList<S> observableList= (ObservableList<S>) list;
			observableList.setListChangeListener(new ListChangedListener<S>()
			{
				public void listChanged(S s)
				{
					valueModelDelegator.fireValueChangeEvent();
				}
			});
		}
	}

	public <S> RepeaterBuilder<S> toList(final List<S> list)
	{
		final ValueModelDelegator<List<S>> valueModelDelegator= new ValueModelDelegator<List<S>>();
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
		return new RepeaterBuilder(valueModelDelegator, template, panel, (TemplateComponentBindingBuilder<VisualPanel>) this);
	}

	public <S> TemplateComponentBindingBuilder<C> toProperty(final Supplier<S> getter)
	{
		return toProperty(getter, (Consumer<S>)null);
	}
	
	public <S> TemplateComponentBindingBuilder<C> toProperty(final Supplier<S> getter, final Consumer<S> setter)
	{
		ValueModelDelegator<S> valueModelDelegator= new ValueModelDelegator<S>(new NullMutableValueModel<S>()
		{
			public S getDelegatedValue()
			{
				if (getter != null)
					return getter.get();
				else
					return null;
			}

			public void setValue(S value)
			{
				if (setter != null)
					setter.accept(value);
			}
		});

		return with(valueModelDelegator);
	}

	private <S> TemplateComponentBindingBuilder<C> with(ValueModelDelegator<S> valueModelDelegator)
	{
		binder.bind(valueModelDelegator).to((HasValue<S>) component);
		BindingSync.addCondition(valueModelDelegator);
		return this;
	}

	public <S> TemplateComponentBindingBuilder<C> to(final ValueSource<S> valueSource)
	{
		ValueModelDelegator<S> valueModelDelegator= new ValueModelDelegator<S>(new NullMutableValueModel<S>()
		{
			public S getDelegatedValue()
			{
				return valueSource.getValue();
			}
		});

		return with(valueModelDelegator);
	}

	public C build()
	{
		if (!built)
		{
			panel.addChild(component);
			built= true;
		}

		return component;
	}

	public <S> TemplateComponentBindingBuilder<C> toProperty(final Object object, final String propertyName)
	{
		return toProperty(new Supplier<S>()
		{
			public S get()
			{
				return (S) object;
			}
		}, propertyName);
	}

	public <S> TemplateComponentBindingBuilder<C> toProperty(final Supplier<?> supplier, final String propertyName)
	{
		return toProperty(new Supplier<S>()
		{
			public S get()
			{
				S propertyValue= (S) ServiceLocator.getInstance().getReflectionService().getPropertyValue(supplier.get(), propertyName);
				return propertyValue;
			}
		}, new Consumer<S>()
		{
			public void accept(S t)
			{
				ServiceLocator.getInstance().getReflectionService().setPropertyValue(supplier.get(), propertyName, t);
			}
		});
	}

	public Template getTemplate()
	{
		return template;
	}

}
