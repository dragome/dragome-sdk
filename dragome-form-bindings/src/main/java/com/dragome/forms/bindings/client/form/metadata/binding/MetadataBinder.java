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

package com.dragome.forms.bindings.client.form.metadata.binding;

import org.w3c.dom.Element;

import com.dragome.forms.bindings.client.binding.AbstractBindingContainer;
import com.dragome.forms.bindings.client.condition.Conditions;
import com.dragome.forms.bindings.client.form.Field;
import com.dragome.forms.bindings.client.form.metadata.Metadata;
import com.dragome.forms.bindings.client.form.metadata.MetadataPlugin;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.model.interfaces.HasEnabled;
import com.dragome.model.interfaces.HasVisible;
import com.dragome.model.interfaces.UIObject;
//import com.google.gwt.user.client.ui.FocusWidget;
//import com.google.gwt.user.client.ui.UIObject;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Jul 13, 2009
 * Time: 12:44:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetadataBinder extends AbstractBindingContainer
{
	private static final MetadataEnabledAction enableUsingMetadataAction= new MetadataEnabledAction();
	private static final MetadataVisibleAction showUsingMetadataAction= new MetadataVisibleAction();

	private static final ShowWhenAction showWhenAction= new ShowWhenAction();
	private static final HideWhenAction hideWhenAction= new HideWhenAction();
	private static final EnableWhenAction enableAction= new EnableWhenAction();
	private static final DisableWhenAction disableAction= new DisableWhenAction();

	/**
	 * Binds all the metadata of the specific field to a widget.  The metadata will only be applied
	 * if the widget has a supported binding.  No errors will be thrown if it doesn't.
	 *
	 * @param field the field that has the metadata.
	 * @return a builder to apply the styles to a widget.
	 */
	public AllMetadataBindingBuilder bindMetadataOf(Field field)
	{
		return new AllMetadataBindingBuilder(this, MetadataPlugin.getMetadata(field));
	}

	/**
	 * @deprecated use {@link Binder} show/hide enable/disable instead.
	 */
	@Deprecated
	public VisibilityBindingBuilder bindVisibilityOf(Field field)
	{
		return new VisibilityBindingBuilder(this, MetadataPlugin.getMetadata(field).getVisibleModel());
	}

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	@Deprecated
	public EnabledBindingBuilder bindEnabledOf(Field field)
	{
		return new EnabledBindingBuilder(this, MetadataPlugin.getMetadata(field).getEnabledModel());
	}

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	@Deprecated
	public EnabledBindingBuilder bindDisabledOf(Field field)
	{
		return new EnabledBindingBuilder(this, Conditions.isNot(MetadataPlugin.getMetadata(field).getEnabledModel()));
	}

	/**
	 * Binds the value of an arbitrary boolean model to the enabledness and/or visibility of
	 * component.
	 *
	 * @param model the value model to bind to.
	 * @deprecated use show/hide enable/disable instead.
	 */
	@Deprecated
	public ValueOfBindingBuilder bindValueOf(ValueModel<Boolean> model)
	{
		return new ValueOfBindingBuilder(this, model);
	}

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	public ConditionBinderBuilder<?> show(HasVisible uiObject)
	{
		return new ConditionBinderBuilder<HasVisible>(this, uiObject, showUsingMetadataAction, showWhenAction);
	}

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	public ConditionBinderBuilder<?> hide(HasVisible uiObject)
	{
		return new ConditionBinderBuilder<HasVisible>(this, uiObject, showUsingMetadataAction, hideWhenAction);
	}

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	//   public ConditionBinderBuilder<?> show(UIObject uiObject)
	//   {
	//      return show(new HasVisibleUiObjectAdapter(uiObject));
	//   }
	//
	//   /**
	//    * @deprecated use {@link Binder}  show/hide enable/disable instead.
	//    */
	//   public ConditionBinderBuilder<?> hide(UIObject uiObject)
	//   {
	//      return hide(new HasVisibleUiObjectAdapter(uiObject));
	//   }

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	public ConditionBinderBuilder<?> show(Element element)
	{
		return show(new HasVisibleElementAdapter(element));
	}

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	public ConditionBinderBuilder<?> hide(Element element)
	{
		return hide(new HasVisibleElementAdapter(element));
	}

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	public ConditionBinderBuilder<?> enable(HasEnabled widget)
	{
		return new ConditionBinderBuilder<HasEnabled>(this, widget, enableUsingMetadataAction, enableAction);
	}

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	//   public ConditionBinderBuilder<?> enable(final FocusWidget widget)
	//   {
	//      // hoping this will all get optimised away...
	//      return enable(new EnabledFocusWidgetAdapter(widget));
	//   }

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	public ConditionBinderBuilder<?> disable(HasEnabled widget)
	{
		return new ConditionBinderBuilder<HasEnabled>(this, widget, enableUsingMetadataAction, disableAction);
	}

	/**
	 * @deprecated use {@link Binder}  show/hide enable/disable instead.
	 */
	//   public ConditionBinderBuilder<?> disable(final FocusWidget widget)
	//   {
	//      // hoping this will all get optimised away...
	//      return disable(new EnabledFocusWidgetAdapter(widget));
	//   }

	//   private static class EnabledFocusWidgetAdapter implements HasEnabled
	//   {
	//      private final FocusWidget widget;
	//
	//      public EnabledFocusWidgetAdapter(FocusWidget widget)
	//      {
	//         this.widget = widget;
	//      }
	//
	//      public void setEnabled(boolean enabled)
	//      {
	//         widget.setEnabled(enabled);
	//      }
	//
	//      public boolean isEnabled()
	//      {
	//         return widget.isEnabled();
	//      }
	//   }

	private static class MetadataEnabledAction implements ConditionBinderMetadataAction<HasEnabled>
	{
		public ValueModel<Boolean> getModel(Metadata metadata)
		{
			return metadata.getEnabledModel();
		}

		public void apply(HasEnabled widget, boolean enabled)
		{
			widget.setEnabled(enabled);
		}
	}

	private static class MetadataVisibleAction implements ConditionBinderMetadataAction<HasVisible>
	{
		public ValueModel<Boolean> getModel(Metadata metadata)
		{
			return metadata.getVisibleModel();
		}

		public void apply(HasVisible widget, boolean visible)
		{
			widget.setVisible(visible);
		}
	}

	private static class ShowWhenAction implements ConditionBinderWidgetAction<HasVisible>
	{
		public void apply(HasVisible target, boolean visible)
		{
			target.setVisible(visible);
		}
	}

	private static class HideWhenAction implements ConditionBinderWidgetAction<HasVisible>
	{
		public void apply(HasVisible target, boolean hidden)
		{
			target.setVisible(!hidden);
		}
	}

	private static class EnableWhenAction implements ConditionBinderWidgetAction<HasEnabled>
	{
		public void apply(HasEnabled target, boolean enabled)
		{
			target.setEnabled(enabled);
		}
	}

	private static class DisableWhenAction implements ConditionBinderWidgetAction<HasEnabled>
	{
		public void apply(HasEnabled target, boolean disabled)
		{
			target.setEnabled(!disabled);
		}
	}

	//   private static class HasVisibleUiObjectAdapter implements HasVisible
	//   {
	//      private final UIObject widget;
	//
	//      public HasVisibleUiObjectAdapter(UIObject widget)
	//      {
	//         this.widget = widget;
	//      }
	//
	//      public void setVisible(boolean visible)
	//      {
	//         widget.setVisible(visible);
	//      }
	//
	//      public boolean isVisible()
	//      {
	//         return widget.isVisible();
	//      }
	//   }

	private static class HasVisibleElementAdapter implements HasVisible
	{
		private Element element;

		public HasVisibleElementAdapter(Element element)
		{
			this.element= element;
		}

		public void setVisible(boolean visible)
		{
			//         UIObject.setVisible(element, visible);
		}

		public boolean isVisible()
		{
			return UIObject.isVisible(element);
		}
	}
}