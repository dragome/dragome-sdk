package com.dragome.forms.bindings.client.bean;

import com.dragome.forms.bindings.client.form.ListModelProvider;
import com.dragome.forms.bindings.client.form.ValueModelProvider;
import com.dragome.forms.bindings.client.value.AbstractMutableValueModel;
import com.dragome.forms.bindings.client.value.DelegatingValueModel;
import com.dragome.forms.bindings.client.value.ValueHolder;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 1, 2010
 * Time: 8:45:59 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBeanModelProvider<B> extends AbstractMutableValueModel<B> implements ValueModelProvider<String>, ListModelProvider<String>, HasDirtyModel
{
	protected DelegatingValueModel<B> source= new DelegatingValueModel<B>(new ValueHolder<B>());
	private PropertyModelRegistry registry= new PropertyModelRegistry();
	private CollectionConverters collectionConverters= new CollectionConverters();
	private ValueHolder<Boolean> autoCommit= new ValueHolder<Boolean>(false);

	protected AbstractBeanModelProvider()
	{
		// Each BeanPropertyValue/ListModel monitors their source model and will automatically
		// readFromSource() when on value changes, so there's not explicit read from source to
		// call here.
		//
		// Our actual value is held in our delegating model so we always
		// fire a value change when it changes.
		source.addValueChangeHandler(new ValueChangeHandler<B>()
		{
			public void onValueChange(ValueChangeEvent<B> event)
			{
				fireValueChangeEvent(event.getValue());
			}
		});
	}

	public B getValue()
	{
		return source.getValue();
	}

	/**
	 * Sets the bean to use as the root for all models created by this provider.  All value models will update after this
	 * method has been called.
	 * <p/>
	 * <b>Please note<b/> if {@link #setBeanSource(com.pietschy.gwt.pectin.client.value.ValueModel)} has been called
	 * with a model that isn't an instance of {@link com.pietschy.gwt.pectin.client.value.MutableValueModel}
	 * then this method will fail.
	 *
	 * @param bean the bean
	 */
	public void setValue(B value)
	{
		source.setValue(value);
	}

	/**
	 * @deprecated use {@link #setValue(B)} instead.
	 */
	@Deprecated
	public void setBean(B bean)
	{
		setValue(bean);
	}

	/**
	 * @deprecated use {@link #getValue()} instead.
	 */
	@Deprecated
	public B getBean()
	{
		return getValue();
	}

	/**
	 * Sets the {@link com.pietschy.gwt.pectin.client.value.ValueModel} to be used as the source of this provider.  All changes to the source
	 * model will be tracked.
	 * <p/>
	 * <b>Please note<b/> that if the source is not an instance of {@link com.pietschy.gwt.pectin.client.value.MutableValueModel}
	 * then any subsequent calls to {@link #setBean(Object)} will fail.
	 *
	 * @param beanSource the {@link com.pietschy.gwt.pectin.client.value.ValueModel} containing the source bean.
	 */
	public void setBeanSource(ValueModel<B> beanSource)
	{
		source.setDelegate(beanSource);
	}

	public void setAutoCommit(boolean autoCommit)
	{
		this.autoCommit.setValue(autoCommit);
	}

	public boolean isAutoCommit()
	{
		return this.autoCommit.getValue();
	}

	/**
	 * Checkpoints the current state of all value models and clears the dirty state of all models.
	 */
	public void checkpoint()
	{
		registry.withEachModel(new PropertyModelVisitor()
		{
			public void visit(BeanPropertyModelBase model)
			{
				model.checkpoint();
			}
		});
	}

	/**
	 * Resets all the models back to their last check pointed state.  If checkpoint hasn't been called then
	 * it will revert to the state when the source bean was last configured.
	 */
	public void revert()
	{
		registry.withEachModel(new PropertyModelVisitor()
		{
			public void visit(BeanPropertyModelBase model)
			{
				model.revert();
			}
		});
	}

	/**
	 * Writes all outstanding changes to the underlying bean graph and clears all the dirty state.
	 */
	public void commit()
	{
		commit(true);
	}

	/**
	 * Writes all outstanding changes to the underlying bean graph.  If <code>checkpoint</code>
	 * is <code>true</code> then the provider will be check pointed and the dirty state cleared.
	 * If <code>false</code> then the changes are still written to the bean graph but the dirty state
	 * remains as is.
	 * <p/>
	 * This method is useful if you don't want to clear the dirty state until some time later, e.g. after
	 * a RPC call has succeeded.
	 *
	 * @param checkpoint <code>true</code> to checkpoing the provider and clear the dirty state, <code>false</code>
	 *                   to leave the dirty state as is.
	 */
	public void commit(final boolean checkpoint)
	{
		registry.withEachModel(new PropertyModelVisitor()
		{
			public void visit(BeanPropertyModelBase model)
			{
				if (model.isMutableProperty())
				{
					model.writeToSource(checkpoint);
				}
			}
		});
	}

	/**
	 *
	 * @deprecated use {@link #dirty()} instead.
	 */
	@Deprecated
	public ValueModel<Boolean> getDirtyModel()
	{
		return dirty();
	}

	/**
	 * Returns a value model that reflects this providers dirty state.
	 * @return a value model that reflects this providers dirty state.
	 */
	public ValueModel<Boolean> dirty()
	{
		return registry.getDirtyModel();
	}

	/**
	 * Gets a {@link com.pietschy.gwt.pectin.client.list.ListModel} based on the specified property name and value type.  Property types
	 * of the generic interface types {@link java.util.Collection}, {@link java.util.List}, {@link java.util.Set}, {@link java.util.SortedSet} are supported
	 * out of the box.  Additional types can be supported by registering a suitable {@link com.pietschy.gwt.pectin.client.bean.CollectionConverter}.
	 * <p/>
	 * Multiple calls to this method will return the same model.
	 *
	 * @param propertyPath the name of the property.
	 * @param elementType  the type contained by the collection
	 * @return the {@link com.pietschy.gwt.pectin.client.list.ListModel} for the specified property.  Multiple calls to this method will return the same
	 *         model.
	 * @throws com.pietschy.gwt.pectin.client.bean.UnknownPropertyException if the bean doesn't define the specified property.
	 * @throws com.pietschy.gwt.pectin.client.bean.UnsupportedCollectionTypeException
	 *                                  if a suitable {@link com.pietschy.gwt.pectin.client.bean.CollectionConverter} hasn't been registered
	 *                                  for the bean property collection type.
	 * @see #registerCollectionConverter(Class, com.pietschy.gwt.pectin.client.bean.CollectionConverter)
	 */
	@SuppressWarnings("unchecked")
	public <T> BeanPropertyListModel<T> getListModel(String propertyPath, Class<T> elementType) throws UnknownPropertyException, UnsupportedCollectionTypeException, IncorrectElementTypeException, NotCollectionPropertyException
	{
		BeanPropertyListModel<?> listModel= registry.getListModel(propertyPath);

		if (listModel == null)
		{
			PropertyDescriptor descriptor= createPropertyDescriptor(propertyPath);

			if (!descriptor.isCollection())
			{
				throw new NotCollectionPropertyException(descriptor);
			}

			Class realElementType= descriptor.getElementType();

			if (!realElementType.equals(elementType))
			{
				throw new IncorrectElementTypeException(descriptor, elementType);
			}

			Class collectionType= descriptor.getValueType();

			CollectionConverter converter= collectionConverters.getConverter(collectionType);

			if (converter == null)
			{
				throw new UnsupportedCollectionTypeException(collectionType);
			}

			listModel= createListModel(getSourceModel(descriptor), descriptor, converter, autoCommit);

			registry.add(propertyPath, listModel);
		}
		else
		{
			// check that the already existing model was created with the same type.
			if (!listModel.getValueType().equals(elementType))
			{
				throw new IllegalArgumentException("ListModel for path `" + propertyPath + "` already created with a different " + "element type: " + listModel.getValueType());
			}
		}

		return (BeanPropertyListModel<T>) listModel;
	}

	protected <T> BeanPropertyListModel<T> createListModel(ValueModel<?> sourceModel, PropertyDescriptor descriptor, CollectionConverter converter, ValueModel<Boolean> autoCommit)
	{
		return new BeanPropertyListModel<T>(sourceModel, descriptor, converter, autoCommit);
	}

	/**
	 * Gets a {@link com.pietschy.gwt.pectin.client.value.ValueModel} for the specified bean property and the specified type.  Multiple calls to this method
	 * will return the same model.
	 *
	 * @param propertyPath the name of the property.
	 * @param valueType    the type of the property.
	 * @return a {@link com.pietschy.gwt.pectin.client.value.ValueModel} for the specified bean property.  Multiple calls to this method
	 *         will return the same model.
	 * @throws com.pietschy.gwt.pectin.client.bean.UnknownPropertyException       if the property isn't defined by the bean.
	 * @throws com.pietschy.gwt.pectin.client.bean.IncorrectPropertyTypeException if the type of the property doesn't match the model type.
	 */
	@SuppressWarnings("unchecked")
	public <T> BeanPropertyValueModel<T> getValueModel(String propertyPath, Class<T> valueType) throws UnknownPropertyException, IncorrectPropertyTypeException
	{
		BeanPropertyValueModel<?> valueModel= registry.getValueModel(propertyPath);

		if (valueModel == null)
		{
			PropertyDescriptor descriptor= createPropertyDescriptor(propertyPath);

			if (!valueType.equals(descriptor.getValueType()))
			{
				throw new IncorrectPropertyTypeException(valueType, descriptor);
			}

			valueModel= createValueModel(getSourceModel(descriptor), descriptor, autoCommit);

			registry.add(propertyPath, valueModel);
		}
		else
		{
			// check that the already existing model was created with the same type.
			if (!valueModel.getValueType().equals(valueType))
			{
				throw new IllegalArgumentException("ValueModel for path `" + propertyPath + "` already created with a different " + "type: " + valueModel.getValueType());
			}
		}

		return (BeanPropertyValueModel<T>) valueModel;
	}

	protected <T> BeanPropertyValueModel<T> createValueModel(ValueModel<?> sourceModel, PropertyDescriptor descriptor, ValueModel<Boolean> autoCommit)
	{
		return new BeanPropertyValueModel<T>(sourceModel, descriptor, autoCommit);
	}

	@SuppressWarnings("unchecked")
	ValueModel<?> getSourceModel(PropertyDescriptor property)
	{
		if (property.isTopLevel())
		{
			return this;
		}
		else
		{
			return getValueModel(property.getParentPath(), property.getBeanType());
		}
	}

	/**
	 * Called the first time a specific property path is requested.
	 *
	 * @param propertyPath the full path of the property
	 * @return a {@link com.pietschy.gwt.pectin.client.bean.PropertyDescriptor} for the specified path.
	 */
	public abstract PropertyDescriptor createPropertyDescriptor(String propertyPath);

	/**
	 * Registers a new converter for converting between collection based bean properties and the ListModel.
	 * This allows uses to support collection types other than the generic interface types on their beans.
	 *
	 * @param collectionClass the collection type of the bean property.
	 * @param converter       the converter to use for bean properties of the specified collection type.
	 */
	public <T> void registerCollectionConverter(Class<T> collectionClass, CollectionConverter<T> converter)
	{
		collectionConverters.register(collectionClass, converter);
	}
}
