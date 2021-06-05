package com.dragome.forms.bindings.builders.helpers;

import java.util.List;

import com.dragome.forms.bindings.builders.ActionExecutor;
import com.dragome.forms.bindings.builders.ChildrenBuilder;
import com.dragome.forms.bindings.builders.ComponentBuilder;
import com.dragome.forms.bindings.builders.Consumer;
import com.dragome.forms.bindings.builders.RepeaterBuilder;
import com.dragome.forms.bindings.builders.Supplier;
import com.dragome.forms.bindings.builders.TemplateBindingBuilder;
import com.dragome.forms.bindings.builders.TemplateComponentBindingBuilder;
import com.dragome.forms.bindings.client.value.ValueSource;
import com.dragome.guia.components.interfaces.VisualButton;
import com.dragome.guia.components.interfaces.VisualCheckbox;
import com.dragome.guia.components.interfaces.VisualComponent;
import com.dragome.guia.components.interfaces.VisualLabel;
import com.dragome.guia.components.interfaces.VisualLink;
import com.dragome.guia.components.interfaces.VisualPanel;
import com.dragome.guia.components.interfaces.VisualTextField;
import com.dragome.guia.events.listeners.interfaces.BlurListener;
import com.dragome.guia.events.listeners.interfaces.DoubleClickListener;
import com.dragome.guia.events.listeners.interfaces.KeyUpListener;

public class BinderHelper
{
	public static ComponentBuilder componentBuilder;
	private static TemplateBindingBuilder templateBindingBuilder;
	private String templateName;
	static TemplateComponentBindingBuilder<? extends VisualComponent> templateComponentBindingBuilder;
	private static VisualComponent component;
	public static Class<VisualPanel> PANEL= VisualPanel.class;
	public static Class<VisualButton> BUTTON= VisualButton.class;
	public static Class<VisualLabel> LABEL= VisualLabel.class;
	public static Class<VisualCheckbox> CHECKBOX= VisualCheckbox.class;
	public static Class<VisualTextField> TEXTFIELD= VisualTextField.class;
	public static Class<VisualLink> LINK= VisualLink.class;

	public BinderHelper(String aChildTemplateName)
	{
		this.templateName= aChildTemplateName;
	}

	public static BinderHelper bind(String aChildTemplateName)
	{
		return new BinderHelper(aChildTemplateName);
	}

	public <C extends VisualComponent> C as(Class<C> componentType, final ActionExecutor actionExecutor)
	{
		TemplateBindingBuilder lastTemplateBindingBuilder= templateBindingBuilder;
		templateBindingBuilder= componentBuilder.bindTemplate(templateName);
		templateComponentBindingBuilder= templateBindingBuilder.as(componentType);
		component= templateComponentBindingBuilder.component();
		templateComponentBindingBuilder.buildChildren(new ChildrenBuilder()
		{
			public void build(ComponentBuilder builder)
			{
				ComponentBuilder lastComponentBuilder= componentBuilder;
				componentBuilder= builder;
				actionExecutor.execute();
				componentBuilder= lastComponentBuilder;
			}
		});
		templateComponentBindingBuilder.build();
		templateBindingBuilder= lastTemplateBindingBuilder;
		return (C) component;
	}

	public static <S, C extends VisualComponent> TemplateComponentBindingBuilder<C> toProperty(final Supplier<S> getter, final Consumer<S> setter)
	{
		return (TemplateComponentBindingBuilder<C>) templateComponentBindingBuilder.toProperty(getter, setter);
	}

	public static <S, C extends VisualComponent> RepeaterBuilderHelper<S, C> toListProperty(final Supplier<List<S>> getter)
	{
		return new RepeaterBuilderHelper<S, C>((RepeaterBuilder<S, C>) templateComponentBindingBuilder.toListProperty(getter));
	}

	public static <S, C extends VisualComponent> TemplateComponentBindingBuilder<C> to(final ValueSource<S> valueSource)
	{
		return (TemplateComponentBindingBuilder<C>) templateComponentBindingBuilder.to(valueSource);
	}

	public static void showWhen(Supplier<Boolean> supplier)
	{
		componentBuilder.showWhen(supplier);
	}

	public static void onClick(final ActionExecutor actionExecutor)
	{
		templateComponentBindingBuilder.onClick(actionExecutor);
	}

	public static void onDoubleClick(final ActionExecutor actionExecutor)
	{
		templateComponentBindingBuilder.onDoubleClick(new DoubleClickListener()
		{
			public void doubleClickPerformed(VisualComponent aVisualComponent)
			{
				actionExecutor.execute();
			}
		});
	}

	public static void onKeyUp(final KeyUpListener keyUpListener, int... codes)
	{
		templateComponentBindingBuilder.onKeyUp(keyUpListener, codes);
	}

	public static void onBlur(BlurListener blurListener)
	{
		templateComponentBindingBuilder.onBlur(blurListener);
	}

	public static ComponentBuilder styleWith(String className)
	{
		return (ComponentBuilder) componentBuilder.styleWith(className);
	}

	public static ComponentBuilder style()
	{
		return (ComponentBuilder) componentBuilder.style(component);
	}

	public static void start(VisualPanel mainPanel)
	{
		componentBuilder= new ComponentBuilder(mainPanel);
	}
}
