package com.dragome.forms.bindings.client.bean;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.dragome.forms.bindings.client.condition.OrFunction;
import com.dragome.forms.bindings.client.value.ReducingValueModel;
import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Mar 26, 2010
 * Time: 11:31:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyModelRegistry
{
	private LinkedHashMap<String, BeanPropertyModelBase> allModels= new LinkedHashMap<String, BeanPropertyModelBase>();

	private HashMap<String, BeanPropertyValueModel<?>> valueModels= new HashMap<String, BeanPropertyValueModel<?>>();
	private HashMap<String, BeanPropertyListModel<?>> listModels= new HashMap<String, BeanPropertyListModel<?>>();

	private ReducingValueModel<Boolean, Boolean> dirtyModel= new ReducingValueModel<Boolean, Boolean>(new OrFunction());

	public ValueModel<Boolean> getDirtyModel()
	{
		return dirtyModel;
	}

	/**
	 * Visits each model in the order it was registered.  This method also holds prevents
	 * any changes to the dirty state until after the visitor has finished.  This is mainly
	 * to prevent the dirty model from recomputing needlessly if the visitor is updating the
	 * dirty state of each model.
	 *
	 * @param visitor the visitor.
	 */
	public void withEachModel(final PropertyModelVisitor visitor)
	{
		// we only update the dirty model after we've finished updating all
		// the models, otherwise we'll get a lot of recomputing for nothing.
		dirtyModel.recomputeAfterRunning(new Runnable()
		{
			public void run()
			{
				// performance could be improved here if the dirty model went deaf for a bit.
				for (BeanPropertyModelBase model : allModels.values())
				{
					visitor.visit(model);
				}
			}
		});
	}

	public BeanPropertyValueModel<?> getValueModel(String fullPath)
	{
		return valueModels.get(fullPath);
	}

	public BeanPropertyListModel<?> getListModel(String fullPath)
	{
		return listModels.get(fullPath);
	}

	public <T> void add(String fullPath, BeanPropertyListModel<T> listModel)
	{
		doAddCommonBits(fullPath, listModel);
		listModels.put(fullPath, listModel);
	}

	public <T> void add(String fullPath, BeanPropertyValueModel<T> valueModel)
	{
		doAddCommonBits(fullPath, valueModel);
		valueModels.put(fullPath, valueModel);
	}

	private void doAddCommonBits(String key, BeanPropertyModelBase valueModel)
	{
		allModels.put(key, valueModel);
		dirtyModel.addSourceModel(valueModel.dirty());
	}

}
