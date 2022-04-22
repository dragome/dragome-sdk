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
package com.dragome.templates;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dragome.guia.GuiaServiceLocator;
import com.dragome.render.ItemProcessorImpl;
import com.dragome.render.interfaces.ItemProcessor;
import com.dragome.render.interfaces.TemplateHandler;
import com.dragome.render.interfaces.View;
import com.dragome.templates.interfaces.SimpleItemProcessor;
import com.dragome.templates.interfaces.Template;

public class TemplateRepeater<T>
{
	public class ItemWrapper<T>
	{
		private T item;

		public int hashCode()
		{
			return item.hashCode();
		}

		public boolean equals(Object obj)
		{
			return ((ItemWrapper) obj).getItem() == item;
		}

		public ItemWrapper(T item)
		{
			this.item= item;
		}

		public T getItem()
		{
			return item;
		}

	}

	private List<T> items;
	private ItemProcessor<T> itemProcessor;
	private Map<ItemWrapper<T>, List<Template>> templatesByItem= new LinkedHashMap<>();
	private LinkedHashMap<ItemWrapper<T>, List<Template>> shownTemplates= new LinkedHashMap<>();
	private boolean updating= false;

	public TemplateRepeater()
	{
	}

	public TemplateRepeater(boolean updating)
	{
		this.updating= updating;
	}

	public TemplateRepeater(List<T> items, final ItemProcessor<T> itemProcessor)
	{
		this.items= items;
		this.itemProcessor= itemProcessor;
	}

	public TemplateRepeater(List<T> items, Template mainTemplate, String subTemplateName, final SimpleItemProcessor<T> simpleItemProcessor, boolean updating)
	{
		this.updating= updating;
		this.items= items;
		itemProcessor= new ItemProcessorImpl<T>(mainTemplate, subTemplateName)
		{
			public void fillTemplate(T item, Template aTemplate)
			{
				simpleItemProcessor.fillTemplate(item, aTemplate);
			}
		};
	}

	public void repeatItems()
	{
		repeatItems(items, itemProcessor);
	}

	public void clearAndRepeatItems()
	{
		//		for (Entry<T, List<Template>> entry : templatesByItem.entrySet())
		//		{
		//			for (Template template : entry.getValue())
		//			{
		//				if (template.getParent() != null)
		//					template.getParent().remove(template);
		//			}
		//		}
		//
		//		templatesByItem.clear();

		repeatItems(items, itemProcessor);
	}

	public void repeatItems(Iterable<T> items, ItemProcessor<T> itemProcessor)
	{
		Map<ItemWrapper<T>, List<Template>> templatesByItemReplacement= new LinkedHashMap<>();
		TemplateHandler templateHandler= GuiaServiceLocator.getInstance().getTemplateHandler();

		Entry<ItemWrapper<T>, List<Template>> entry= null;
		Iterator<Entry<ItemWrapper<T>, List<Template>>> entriesIterator= shownTemplates.entrySet().iterator();

		if (updating)
		{
			if (!shownTemplates.isEmpty())
				entry= entriesIterator.next();
		}

		for (T item : items)
		{
			TemplateRepeater<T>.ItemWrapper<T> itemWrapper= new ItemWrapper<T>(item);
			List<Template> templatesOfItem= templatesByItem.get(itemWrapper);

			if (templatesOfItem == null || templatesOfItem.isEmpty())
			{
				List<Template> repeatChildren= itemProcessor.getRepeatTemplates(item);
				for (Template template : repeatChildren)
					templateHandler.makeInvisible(template);

				templatesOfItem= templateHandler.cloneTemplates(repeatChildren);
				insertTemplates(itemProcessor, itemWrapper, templatesOfItem, entry);

				for (Template repeatChild : templatesOfItem)
					templateHandler.makeVisible(repeatChild);

				itemProcessor.fillTemplates(item, templatesOfItem);
				if (templatesOfItem.size() == 1)
					itemProcessor.fillTemplate(item, templatesOfItem.get(0));
				else
					System.out.println("more than one template");
			}
			else if (entry == null || !entry.getKey().equals(itemWrapper))
				insertTemplates(itemProcessor, itemWrapper, templatesOfItem, entry);

			templatesByItemReplacement.put(new ItemWrapper(item), templatesOfItem);

			if (updating)
				if (entriesIterator.hasNext())
				{
					if (entry == null || entry.getKey().equals(itemWrapper))
						entry= entriesIterator.next();
				}
				else
					entry= null;
		}

		shownTemplates= new LinkedHashMap<ItemWrapper<T>, List<Template>>(templatesByItemReplacement);

		if (updating)
		{
			for (Entry<TemplateRepeater<T>.ItemWrapper<T>, List<Template>> entry2 : templatesByItem.entrySet())
			{
				if (!templatesByItemReplacement.containsKey(entry2.getKey()))
				{
					templatesByItemReplacement.put(entry2.getKey(), entry2.getValue());
					for (Template template : entry2.getValue())
					{
						if (template.getParent() != null)
							template.getParent().remove(template);
					}
				}
			}
			templatesByItem= templatesByItemReplacement;
		}

	}
	private void insertTemplates(ItemProcessor<T> itemProcessor, ItemWrapper<T> item, List<Template> clonedRepeatChildren, Entry<ItemWrapper<T>, List<Template>> entry)
	{
		Template insertChild= entry != null ? entry.getValue().get(0) : itemProcessor.getInsertTemplate(item.getItem());
		for (Template clonedRepeatedChild : clonedRepeatChildren)
		{
			clonedRepeatedChild.updateName(clonedRepeatedChild.getName() + "_" + Integer.toHexString((int) System.currentTimeMillis()));
			insertChild.getParent().insertBefore(clonedRepeatedChild, insertChild);
		}
	}

	public ItemProcessorImpl<T> createItemAdder(Template aTemplate, final Template originalTemplate, final List<? extends View<T>> modelViews, String insertionPoint)
	{
		return new ItemProcessorImpl<T>(aTemplate, insertionPoint)
		{
			public void fillTemplates(final T item, List<Template> aRowTemplate)
			{
				View<T> result= findViewFor(item);
				result.composeView(item, aRowTemplate);
			}

			public List<Template> getRepeatTemplates(T item)
			{
				View<T> result= findViewFor(item);
				return result.getRequiredSubTemplates(originalTemplate);
			}

			private View<T> findViewFor(T item)
			{
				for (View<T> modelView : modelViews)
					if (modelView.match(item, originalTemplate))
						return modelView;

				return null;
			}
		};
	}

	public Template getUpdatedTemplateFor(T item)
	{
		TemplateHandler templateHandler= GuiaServiceLocator.getInstance().getTemplateHandler();
		Template updatedTemplate= itemProcessor.getRepeatTemplates(item).get(0);
		Template clone= templateHandler.clone(updatedTemplate);
		templateHandler.makeVisible(clone);

		templatesByItem.get(item).add(clone);
		return clone;
	}
}
