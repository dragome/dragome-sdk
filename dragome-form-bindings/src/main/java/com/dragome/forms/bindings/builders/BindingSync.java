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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Supplier;

import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.annotations.DragomeCompilerSettings;

public class BindingSync
{
	static Queue<MethodVisitedEvent> events= new LinkedList<MethodVisitedEvent>();
	private static List<ValueModelDelegator<?>> conditions= new ArrayList<ValueModelDelegator<?>>();
	static boolean firing;
	public static Stack<NullMutableValueModel<?>> recordingFor= new Stack<NullMutableValueModel<?>>();

	@DragomeCompilerSettings(CompilerType.Strict)
	public static void fireChanges()
	{
		if (!firing)
		{
			try
			{
				firing= true;

				MethodVisitedEvent event= events.peek();
				while (event != null)
				{
					List<ValueModelDelegator<?>> conditions2= new ArrayList<ValueModelDelegator<?>>(conditions);
					for (ValueModelDelegator<?> valueModelDelegator : conditions2)
					{
						if (valueModelDelegator.getValueSource() instanceof NullMutableValueModel)
						{
							NullMutableValueModel<Object> nullMutableValueModel= (NullMutableValueModel) valueModelDelegator.getValueSource();
							List<MethodVisitedEvent> methodVisitedEvents= new ArrayList<MethodVisitedEvent>(nullMutableValueModel.getMethodVisitedEvents());
							for (MethodVisitedEvent methodVisitedEvent : methodVisitedEvents)
								if (methodVisitedEvent.isSameProperty(event))
									valueModelDelegator.fireValueChangeEvent();
						}
					}

					events.poll();
					event= events.peek();
				}
			}
			finally
			{
				firing= false;
			}
		}
	}

	public static <V> ValueModelDelegator<V> createCondition(final Supplier<V> object)
	{
		ValueModelDelegator<V> condition= new ValueModelDelegator<V>(new NullMutableValueModel<V>()
		{
			public V getDelegatedValue()
			{
				return object.get();
			}
		});

		addCondition(condition);
		return (ValueModelDelegator<V>) condition;
	}

	public static void addCondition(ValueModelDelegator<?> condition)
	{
		conditions.add(condition);
	}

	public static void addEvent(MethodVisitedEvent methodVisitedEvent)
	{
		if (!events.contains(methodVisitedEvent))
			events.add(methodVisitedEvent);
	}

}
