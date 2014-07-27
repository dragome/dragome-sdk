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

package com.dragome.forms.bindings.client.form.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.dragome.forms.bindings.client.form.Field;
import com.dragome.forms.bindings.client.form.FieldModel;
import com.dragome.forms.bindings.client.form.FormModel;
import com.dragome.forms.bindings.client.form.FormattedFieldModel;
import com.dragome.forms.bindings.client.form.metadata.binding.MetadataBindingBuilder;

/**
 * 
 */
public class MetadataPlugin
{
	public static final String DEFAULT_WATERMARK_STYLE= "pectin-Watermark";

	public static <T> MetadataBindingBuilder<T> enable(Field field, Field... others)
	{
		return enable(toCollection(field, others));
	}

	public static <T> MetadataBindingBuilder<T> enable(Collection<Field<?>> fields)
	{
		return new MetadataBindingBuilder<T>(fields, new MetadataBindingBuilder.Action()
		{
			public void apply(Metadata metadata, boolean value)
			{
				metadata.setEnabled(value);
			}
		});
	}

	public static <T> MetadataBindingBuilder<T> disable(Field field, Field... others)
	{
		return disable(toCollection(field, others));
	}

	public static <T> MetadataBindingBuilder<T> disable(Collection<Field<?>> fields)
	{
		return new MetadataBindingBuilder<T>(fields, new MetadataBindingBuilder.Action()
		{
			public void apply(Metadata metadata, boolean value)
			{
				metadata.setEnabled(!value);
			}
		});
	}

	public static <T> MetadataBindingBuilder<T> show(Field field, Field... others)
	{
		return show(toCollection(field, others));
	}

	public static <T> MetadataBindingBuilder<T> show(Collection<Field<?>> fields)
	{
		return new MetadataBindingBuilder<T>(fields, new MetadataBindingBuilder.Action()
		{
			public void apply(Metadata metadata, boolean value)
			{
				metadata.setVisible(value);
			}
		});
	}

	public static <T> MetadataBindingBuilder<T> hide(Field field, Field... others)
	{
		return hide(toCollection(field, others));
	}

	public static <T> MetadataBindingBuilder<T> hide(Collection<Field<?>> fields)
	{
		return new MetadataBindingBuilder<T>(fields, new MetadataBindingBuilder.Action()
		{
			public void apply(Metadata metadata, boolean value)
			{
				metadata.setVisible(!value);
			}
		});
	}

	public static WatermarkBuilder watermark(FieldModel<String> field)
	{
		return new WatermarkBuilder(getMetadata(field));
	}

	public static WatermarkBuilder watermark(FieldModel<String> field, FieldModel<String>... others)
	{
		return new WatermarkBuilder(getAllMetadata(toCollection(field, others)));
	}

	public static WatermarkBuilder watermark(FormattedFieldModel<?> field)
	{
		return new WatermarkBuilder(getMetadata(field));
	}

	public static WatermarkBuilder watermark(FormattedFieldModel<?> field, FormattedFieldModel<?>... others)
	{
		return new WatermarkBuilder(getAllMetadata(toCollection(field, others)));
	}

	/**
	 * Gets a builder for creating {@link com.pietschy.gwt.pectin.client.value.ValueModel} for the various
	 * metadata states. E.g.
	 * <pre>
	 * binder.disable(someWidget).when(metadataOf(someField).isDisabled());
	 * </pre>
	 * @param field the field of interest.
	 * @return and new builder.
	 */
	public static MetadataConditionBuilder metadataOf(Field field)
	{
		return new MetadataConditionBuilder(getMetadata(field));
	}

	private static Collection<Field<?>> toCollection(Field<?> field, Field<?>... others)
	{
		ArrayList<Field<?>> fields= new ArrayList<Field<?>>();
		fields.add(field);
		fields.addAll(Arrays.asList(others));
		return fields;
	}

	private static Collection<Metadata> getAllMetadata(Collection<Field<?>> fields)
	{
		ArrayList<Metadata> result= new ArrayList<Metadata>();
		for (Field other : fields)
		{
			result.add(getMetadata(other));
		}

		return result;
	}

	public static Metadata getMetadata(Field field)
	{
		return getMetadataManager(field.getFormModel()).getMetadata(field);
	}

	public static MetadataManager getMetadataManager(FormModel form)
	{
		MetadataManager manager= (MetadataManager) form.getProperty(MetadataManager.class);
		if (manager == null)
		{
			manager= new MetadataManager();
			form.putProperty(MetadataManager.class, manager);
			form.addBindingCallback(manager);
		}

		return manager;
	}

	/**
	 * This method ensures that the metadata binding callbacks are installed.  The callbacks ensure that the Metadata
	 * plugin is activated every time {@link com.pietschy.gwt.pectin.client.binding.FormBinder} is used.
	 * <p>
	 * This method is only needed if
	 * you haven't called any of the {@link #enable(com.pietschy.gwt.pectin.client.form.Field , com.pietschy.gwt.pectin.client.form.Field[])} style methods
	 * or the {@link #getMetadata(com.pietschy.gwt.pectin.client.form.Field)} or the
	 * {@link #getMetadataManager(com.pietschy.gwt.pectin.client.form.FormModel)} method.
	 * <p>
	 * Typically you'd use this if you want the metadata plugin automatically configure widgets based only on
	 * the state of {@link com.pietschy.gwt.pectin.client.form.Field#isMutableSource()}.
	 *
	 * @param form the form.
	 */
	public static void ensureMetadataBindingsInstalled(FormModel form)
	{
		getMetadataManager(form);
	}

}
