package com.dragome.forms.bindings.builders;

import com.dragome.forms.bindings.client.form.binding.FormBinder;
import com.dragome.forms.bindings.client.form.metadata.binding.ConditionBinderBuilder;
import com.dragome.forms.bindings.client.style.StyleBinder;
import com.dragome.forms.bindings.client.style.StyleBindingBuilder;
import com.dragome.forms.bindings.client.style.StyleBuilder;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.pectin.VisualComponentHasEnabled;

public abstract class BaseBuilder<C extends VisualComponent, B extends BaseBuilder<C, B>>
{
    protected ConditionBinderBuilder<?> conditionBinderBuilder;
    protected FormBinder binder= new FormBinder();
    protected C component;

    private StyleBinder styleBinder= new StyleBinder();
    private StyleBinder styleBinder2= new StyleBinder();
    private StyleBuilder styleBuilder;
    private StyleBuilder styleBuilder2;
    private StyleBindingBuilder styleBindingBuilder;
    private StyleBindingBuilder styleBindingBuilder2;

    public BaseBuilder()
    {
    }

    public B show(C visualComponent)
    {
	conditionBinderBuilder= binder.show(visualComponent);
	return (B) this;
    }

    public B styleWith(String trueClassName, String falseClassName)
    {
	B style= style(component);
	return style.with(trueClassName, falseClassName);
    }

    public B styleWith(String className)
    {
	return style(component).with(className);
    }

    public B showWhen(Supplier<Boolean> supplier)
    {
	return show(component).when(supplier);
    }

    public B disable(C visualComponent)
    {
	conditionBinderBuilder= binder.disable(new VisualComponentHasEnabled(component));
	return (B) this;
    }

    public B disableWhen(Supplier<Boolean> supplier)
    {
	return disable(component).when(supplier);
    }

    public B with(String className)
    {
	styleBindingBuilder= styleBuilder.with(className);

	return (B) this;
    }

    public B style(VisualComponent component)
    {
	styleBuilder= styleBinder.style(component);
	styleBuilder2= styleBinder2.style(component);
	return (B) this;
    }

    public B when(Supplier<Boolean> object)
    {
	ValueModelDelegator<Boolean> condition= BindingSync.createCondition(object);

	if (conditionBinderBuilder != null)
	{
	    conditionBinderBuilder.when(condition);
	    conditionBinderBuilder= null;
	}
	else
	{
	    styleBindingBuilder.when(BindingSync.createCondition(object));
	    styleBindingBuilder= null;
	}
	
	return (B) this;
    }

    public B with(String trueClassName, String falseClassName)
    {
	styleBindingBuilder= styleBuilder.with(trueClassName);
	styleBindingBuilder2= styleBuilder2.with(falseClassName);
	return (B) this;
    }

    public B accordingTo(final Supplier<Boolean> object)
    {
	styleBindingBuilder.when(BindingSync.createCondition(object));
	styleBindingBuilder2.when(BindingSync.createCondition(new Supplier<Boolean>()
	{
	    public Boolean get()
	    {
		return !object.get();
	    }
	}));
	
	return (B) this;
    }

    public abstract C build();

}