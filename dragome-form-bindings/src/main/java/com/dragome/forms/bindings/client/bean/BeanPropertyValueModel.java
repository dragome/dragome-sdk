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

package com.dragome.forms.bindings.client.bean;

import com.dragome.forms.bindings.client.value.AbstractMutableValueModel;
import com.dragome.forms.bindings.client.value.ValueHolder;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 *
 */
public class BeanPropertyValueModel<T> extends AbstractMutableValueModel<T> implements BeanPropertyModelBase, HasMutableModel
{
	private PropertyDescriptor propertyDescriptor;
	private T checkpointValue;
	private T currentValue;
	private ValueHolder<Boolean> dirtyModel= new ValueHolder<Boolean>(false);
	private ValueHolder<Boolean> mutableModel= new ValueHolder<Boolean>(false);
	private ValueModel<?> source;
	private ValueModel<Boolean> autoCommit;
	private UpdateStrategy<T> defaultUpdateStrategy= new DefaultUpdateStrategy();
	private UpdateStrategy<T> autoCommitUpdateStrategy= new AutoCommitUpdateStrategy();

	public BeanPropertyValueModel(ValueModel<?> sourceModel, PropertyDescriptor descriptor, ValueModel<Boolean> autoCommit)
	{
		this.source= sourceModel;
		this.propertyDescriptor= descriptor;
		this.autoCommit= autoCommit;
		dirtyModel.setFireEventsEvenWhenValuesEqual(false);

		installValueChangeHandler(source);
		handleSourceModelChange();
	}

	@SuppressWarnings("unchecked")
	private void installValueChangeHandler(ValueModel<?> source)
	{
		// yep I know, no generics... I don't know or care what the type is since the generated accessor handles
		// all that.  We could get around this if ValueChangeHandler allowed for ValueChangeHandler<? super T>.
		source.addValueChangeHandler(new ValueChangeHandler()
		{
			public void onValueChange(ValueChangeEvent event)
			{
				handleSourceModelChange();
			}
		});
	}

	private void handleSourceModelChange()
	{
		readFromSource();
		onSourceModelChanged(source.getValue());
	}

	/**
	 * This is an empty method that subclasses can override to perform
	 * actions when the source bean changes.
	 *
	 * @param sourceBean the new value of the source bean.
	 */
	protected void onSourceModelChanged(Object sourceBean)
	{
	}

	public String getPropertyName()
	{
		return propertyDescriptor.getPropertyName();
	}

	public Class getValueType()
	{
		return propertyDescriptor.getValueType();
	}

	protected boolean isAutoCommit()
	{
		// only true if not null and true.
		return Boolean.TRUE.equals(autoCommit.getValue());
	}

	private void ensureMutable()
	{
		if (!isMutableProperty())
		{
			throw new ReadOnlyPropertyException(propertyDescriptor);
		}
		else if (!isNonNullSource())
		{
			throw new SourceBeanIsNullException(propertyDescriptor);
		}
	}

	public void setValue(T value)
	{
		getUpdateStrategy().setValue(value);
	}

	public T getValue()
	{
		return getUpdateStrategy().getValue();
	}

	public void writeToSource(boolean checkpoint)
	{
		getUpdateStrategy().writeToSource(checkpoint);
	}

	public void readFromSource()
	{
		getUpdateStrategy().readFromSource();
	}

	/**
	 * Checkpoints the models dirty state to the current value of the model.  After calling this
	 * method the dirty state will be <code>false</code>.
	 *
	 * @see revertToCheckpoint()
	 */
	public void checkpoint()
	{
		getUpdateStrategy().checkpoint();
	}

	/**
	 * Reverts the value of this model to the previous checkpoint.  If checkpoint hasn't been called
	 * then it will revert to the last call to readFrom.
	 */
	public void revert()
	{
		getUpdateStrategy().revertToCheckpoint();
	}

	public ValueModel<Boolean> mutable()
	{
		return mutableModel;
	}

	public boolean isMutable()
	{
		return mutable().getValue();
	}

	private void updateMutableState()
	{
		mutableModel.setValue(isMutableProperty() && isNonNullSource());
	}

	private boolean isNonNullSource()
	{
		return source.getValue() != null;
	}

	public boolean isMutableProperty()
	{
		return propertyDescriptor.isMutable();
	}

	/**
	 * @deprecated use {@link #dirty()} instead.
	 */
	@Deprecated
	public ValueModel<Boolean> getDirtyModel()
	{
		return dirty();
	}

	public ValueModel<Boolean> dirty()
	{
		return dirtyModel;
	}

	protected UpdateStrategy<T> getUpdateStrategy()
	{
		return isAutoCommit() ? autoCommitUpdateStrategy : defaultUpdateStrategy;
	}

	private interface UpdateStrategy<T>
	{
		void readFromSource();

		void writeToSource(boolean checkpoint);

		void setValue(T value);

		void checkpoint();

		void revertToCheckpoint();

		T getValue();
	}

	private class DefaultUpdateStrategy implements UpdateStrategy<T>
	{
		@SuppressWarnings("unchecked")
		public void readFromSource()
		{
			checkpointValue= (T) propertyDescriptor.readProperty(source.getValue());
			updateMutableState();
			setValueInternal(checkpointValue);
		}

		public void writeToSource(boolean checkpoint)
		{
			ensureMutable();
			T value= getValue();
			propertyDescriptor.writeProperty(source.getValue(), value);
			if (checkpoint)
			{
				checkpoint();
			}
		}

		public void setValue(T value)
		{
			ensureMutable();
			setValueInternal(value);
		}

		public T getValue()
		{
			return currentValue;
		}

		public void checkpoint()
		{
			checkpointValue= getValue();
			dirtyModel.setValue(false);
		}

		public void revertToCheckpoint()
		{
			setValueInternal(checkpointValue);
		}

		protected void setValueInternal(T value)
		{
			T oldValue= currentValue;
			currentValue= value;
			fireValueChangeEvent(oldValue, value);
			afterMutate();
		}

		protected void afterMutate()
		{
			updateDirtyState();
		}

		void updateDirtyState()
		{
			dirtyModel.setValue(computeDirty());
		}

		boolean computeDirty()
		{
			return currentValue == null ? checkpointValue != null : !currentValue.equals(checkpointValue);
		}

	}

	private class AutoCommitUpdateStrategy extends DefaultUpdateStrategy
	{
		boolean inReadFromSource= false;

		@Override
		public void readFromSource()
		{
			// this is a bit of a hack, but we flag that we're inside a
			// readFromSource call so that we can re-use setValueInternal without
			// causing a write back to the source.
			boolean oldInReadFromSource= inReadFromSource;
			inReadFromSource= true;
			try
			{
				super.readFromSource();
			}
			finally
			{
				inReadFromSource= oldInReadFromSource;
			}
		}

		@Override
		protected void afterMutate()
		{
			// We don't write if we're currently reading from the source;
			if (!inReadFromSource)
			{
				// we don't checkpoint so the user can still checkpoint and revert
				// even though were writing through to the bean.
				writeToSource(false);
			}
		}

		@Override
		boolean computeDirty()
		{
			// we're never dirty...
			return false;
		}
	}
}