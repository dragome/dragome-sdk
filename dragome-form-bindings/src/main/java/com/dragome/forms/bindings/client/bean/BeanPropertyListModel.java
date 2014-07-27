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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.dragome.forms.bindings.client.list.ArrayListModel;
import com.dragome.forms.bindings.client.value.ValueHolder;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;

/**
 *
 */
public class BeanPropertyListModel<T> extends ArrayListModel<T> implements BeanPropertyModelBase
{
	private List<T> EMPTY_LIST= Collections.emptyList();

	private List<T> checkpointValue= null;
	private CollectionConverter listConverter;
	private ValueHolder<Boolean> dirtyModel= new ValueHolder<Boolean>(false);
	private ValueHolder<Boolean> mutableModel= new ValueHolder<Boolean>(false);
	private ValueModel<?> source;
	private ValueModel<Boolean> autoCommit;
	private UpdateStrategy<T> defaultUpdateStrategy= new DefaultUpdateStrategy();
	private UpdateStrategy<T> autoCommitUpdateStrategy= new AutoCommitUpdateStrategy();
	private PropertyDescriptor propertyDescriptor;

	public BeanPropertyListModel(ValueModel<?> sourceModel, PropertyDescriptor descriptor, CollectionConverter converter, ValueModel<Boolean> autoCommit)
	{
		this.source= sourceModel;
		this.propertyDescriptor= descriptor;
		this.listConverter= converter;
		this.autoCommit= autoCommit;
		dirtyModel.setFireEventsEvenWhenValuesEqual(false);

		installValueChangeHandler();
		handleSourceModelChange();
	}

	@SuppressWarnings("unchecked")
	private void installValueChangeHandler()
	{
		// yep I know, no generics... I don't know or care what the type is since the accessor handles
		// all that.  And since ValueChangeHandler doesn't allow for ValueChangeHander<? super T> I can't
		// add a ValueChangeHandler<Object> to a ValueModel<?>
		this.source.addValueChangeHandler(new ValueChangeHandler()
		{
			public void onValueChange(ValueChangeEvent bValueChangeEvent)
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
		return propertyDescriptor.getElementType();
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

	@SuppressWarnings("unchecked")
	public void readFromSource()
	{
		getUpdateStrategy().readFromSource();
	}

	public void writeToSource(boolean checkpoint)
	{
		getUpdateStrategy().writeToSource(checkpoint);
	}

	@Override
	public void setElements(Collection<? extends T> elements)
	{
		getUpdateStrategy().setElements(elements);
	}

	@Override
	public void clear()
	{
		getUpdateStrategy().clear();
	}

	@Override
	public void remove(T element)
	{
		getUpdateStrategy().remove(element);
	}

	@Override
	public void add(T element)
	{
		getUpdateStrategy().add(element);
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

	/**
	 * Checkpoints the models dirty state to the current value of the model.  After calling this
	 * method the dirty state will be <code>false</code>.
	 *
	 * @see #revert()
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
		getUpdateStrategy().revert();
	}

	public ValueModel<Boolean> getMutableModel()
	{
		return mutableModel;
	}

	public boolean isMutable()
	{
		return getMutableModel().getValue();
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

	private UpdateStrategy<T> getUpdateStrategy()
	{
		return isAutoCommit() ? autoCommitUpdateStrategy : defaultUpdateStrategy;
	}

	private interface UpdateStrategy<T>
	{
		void readFromSource();

		void writeToSource(boolean checkpoint);

		void setElements(Collection<? extends T> elements);

		void add(T value);

		void remove(T value);

		void clear();

		void checkpoint();

		void revert();
	}

	public class DefaultUpdateStrategy implements UpdateStrategy<T>
	{
		@SuppressWarnings("unchecked")
		public void readFromSource()
		{
			Object propertyValue= propertyDescriptor.readProperty(source.getValue());
			updateElements(toList(listConverter.fromBean(propertyValue)));
			checkpoint();
			updateMutableState();
			afterMutate();
		}

		public void writeToSource(boolean checkpoint)
		{
			ensureMutable();
			propertyDescriptor.writeProperty(source.getValue(), listConverter.toBean(asUnmodifiableList()));
			if (checkpoint)
			{
				checkpoint();
			}
		}

		private void updateElements(Collection<? extends T> elements)
		{
			BeanPropertyListModel.super.setElements(elements);
		}

		public void setElements(Collection<? extends T> elements)
		{
			ensureMutable();
			updateElements(elements);
			afterMutate();
		}

		public void add(T element)
		{
			ensureMutable();
			BeanPropertyListModel.super.add(element);
			afterMutate();
		}

		public void remove(T element)
		{
			ensureMutable();
			BeanPropertyListModel.super.remove(element);
			afterMutate();
		}

		public void clear()
		{
			ensureMutable();
			BeanPropertyListModel.super.clear();
			afterMutate();
		}

		protected void afterMutate()
		{
			updateDirtyState();
		}

		public void checkpoint()
		{
			// we copy so mutations don't affect us.
			checkpointValue= new ArrayList<T>(asUnmodifiableList());
			dirtyModel.setValue(false);
		}

		public void revert()
		{
			// setElements makes a copy of the data (i.e it doesn't maintain a reference
			// to the list so we don't need copy it passing in).
			setElements(getCheckpoint());
		}

		/**
		 * Returns the last checkpoint value.
		 *
		 * @return the checkpoint value.
		 */
		private List<T> getCheckpoint()
		{
			return checkpointValue != null ? checkpointValue : EMPTY_LIST;
		}

		/**
		 * Always returns a non-null list from the specified collection.  If the collection is
		 * and instance of List the the original list is returned, if null then EMPTY_LIST is returned,
		 * otherwise a new ArrayList containing the original collections elements is returned.
		 *
		 * @param collection the collection.
		 * @return the collection if it is a list, EMPTY_LIST if the collection is null, or a new
		 *         ArrayList containing the elements of the collection.
		 */
		private List<T> toList(Collection<T> collection)
		{
			if (collection instanceof List)
			{
				return (List<T>) collection;
			}
			else
			{
				return collection != null ? new ArrayList<T>(collection) : EMPTY_LIST;
			}
		}

		void updateDirtyState()
		{
			dirtyModel.setValue(computeDirty());
		}

		protected boolean computeDirty()
		{
			if (checkpointValue == null)
			{
				return size() != 0;
			}
			else if (size() != checkpointValue.size())
			{
				return true;
			}
			else
			{
				// the sizes are equal so we check the contents are the same.
				for (int i= 0; i < checkpointValue.size(); i++)
				{
					if (!areEqual(get(i), checkpointValue.get(i)))
					{
						return true;
					}
				}
				return false;
			}
		}
	}

	private class AutoCommitUpdateStrategy extends DefaultUpdateStrategy
	{

		private boolean inReadFromSource= false;

		@Override
		public void readFromSource()
		{
			boolean oldInReadFromSource= inReadFromSource;
			try
			{
				inReadFromSource= true;
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
			// only write changes to the model back if we're not
			// in the process of reading it out.
			if (!inReadFromSource)
			{
				writeToSource(false);
			}
		}

		@Override
		protected boolean computeDirty()
		{
			// we're never dirty
			return false;
		}
	}
}