package com.dragome.forms.bindings.builders;

import java.util.ArrayList;
import java.util.List;

import com.dragome.forms.bindings.client.form.binding.FormBinder;
import com.dragome.forms.bindings.client.form.metadata.binding.ConditionBinderBuilder;
import com.dragome.forms.bindings.client.style.StyleBinder;
import com.dragome.forms.bindings.client.style.StyleBindingBuilder;
import com.dragome.forms.bindings.client.style.StyleBuilder;
import com.dragome.model.interfaces.ValueChangeEvent;
import com.dragome.model.interfaces.ValueChangeHandler;
import com.dragome.model.interfaces.VisualComponent;
import com.dragome.model.interfaces.VisualPanel;
import com.dragome.model.listeners.BlurListener;
import com.dragome.model.listeners.ClickListener;
import com.dragome.model.listeners.DoubleClickListener;
import com.dragome.model.listeners.InputListener;
import com.dragome.model.listeners.KeyUpListener;
import com.dragome.model.pectin.VisualComponentHasEnabled;

public abstract class BaseBuilder<C extends VisualComponent, B extends BaseBuilder<C, B>>
{
    protected BaseBuilder<? extends VisualComponent, B> parentBuilder;
    public BaseBuilder<? extends VisualComponent, B> getParentBuilder()
    {
        return parentBuilder;
    }

    public void setParentBuilder(BaseBuilder<? extends VisualComponent, B> parentBuilder)
    {
        this.parentBuilder= parentBuilder;
    }

    protected ConditionBinderBuilder<?> conditionBinderBuilder;
    protected FormBinder binder= new FormBinder();
    protected C component;

    private StyleBinder styleBinder= new StyleBinder();
    private StyleBinder styleBinder2= new StyleBinder();
    private StyleBuilder styleBuilder;
    private StyleBuilder styleBuilder2;
    private StyleBindingBuilder styleBindingBuilder;
    private StyleBindingBuilder styleBindingBuilder2;
    
    private VisualComponent defaultSwitch;
    private ValueModelDelegator<Object> switchSource;
    private List<Case> cases= new ArrayList<Case>();
    private boolean buildEnding= false;
    

    public BaseBuilder()
    {
    }

    public BaseBuilder<? extends VisualComponent, B> parentBuilder()
    {
	return parentBuilder;
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
	build();
	
	ValueModelDelegator<Boolean> condition= BindingSync.createCondition(object);

	if (conditionBinderBuilder != null)
	{
	    conditionBinderBuilder.when(condition);
	    conditionBinderBuilder= null;
	}
	else
	{
	    styleBindingBuilder.when(condition);
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
	build();
	
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
    
    public <S> B switchUsing(Supplier<S> supplier)
    {
	this.switchSource= (ValueModelDelegator<Object>) BindingSync.createCondition(supplier);
	this.switchSource.addValueChangeHandler(new ValueChangeHandler<Object>()
	{
	    public void onValueChange(ValueChangeEvent<Object> event)
	    {
		updateCases();
	    }
	});
	return (B) this;
    }


    public B switchDefault()
    {
	parentBuilder.setDefaultSwitch(component);

	//	Object value= parentBuilder.switchSource.getValue();

	return (B) this;
    }

    public void setDefaultSwitch(VisualComponent defaultSwitch)
    {
	this.defaultSwitch= defaultSwitch;
    }

    public <S> B switchWhen(final Supplier<S> getter)
    {
	parentBuilder.addCase(new Case(getter, component));
	return (B) this;
    }

    public void addCase(Case aCase)
    {
	cases.add(aCase);
    }

    public void updateCases()
    {
	if (switchSource != null)
	{
	    if (defaultSwitch != null)
	    {
		defaultSwitch.setVisible(false);
		Object value= switchSource.getValue();

		VisualComponent found= defaultSwitch;
		for (Case currentCase : cases)
		{
		    currentCase.getComponent().setVisible(false);

		    if (currentCase.getSupplier().get().equals(value))
			found= currentCase.getComponent();
		}

		found.setVisible(true);
//		defaultSwitch= null;
//		cases.clear();
	    }
	}
    }
 
    
    public abstract C build();

    public ComponentBuilder childrenBuilder()
    {
	return new ComponentBuilder((VisualPanel) component, this);
    }
    
    public ComponentBuilder buildChildren(ChildrenBuilder childrenBuilder)
    {
	if (!buildEnding)
	    build();

	ComponentBuilder builder= childrenBuilder();
	childrenBuilder.build(builder);

	if (buildEnding)
	    build();

	updateCases();

	return builder;
    }
    
    public B onDoubleClick(DoubleClickListener doubleClickPerformed)
    {
	component.addDoubleClickListener(doubleClickPerformed);
	return (B) this;
    }

    public B onClick(ClickListener clickListener)
    {
	component.addClickListener(clickListener);
	return (B) this;
    }

    public B onClick(final ActionExecutor actionExecutor)
    {
	component.addClickListener(new ClickListener()
	{
	    public void clickPerformed(VisualComponent aVisualComponent)
	    {
		actionExecutor.execute();
	    }
	});
	return (B) this;
    }

    public VisualComponent component()
    {
	return component;
    }

    public B onKeyUp(final KeyUpListener keyUpListener, final int... codes)
    {
	component.addKeyListener(new KeyUpListener()
	{
	    public void keyupPerformed(VisualComponent visualComponent, int keyCode)
	    {
		for (int code : codes)
		{
		    if (code == keyCode || code == 0)
		    {
			keyUpListener.keyupPerformed(visualComponent, keyCode);
			break;
		    }
		}
	    }
	});
	return (B) this;
    }

    public B onBlur(BlurListener blurListener)
    {
	component.addListener(BlurListener.class, blurListener);
	return (B) this;
    }
    
    public B onInput(InputListener inputListener)
    {
	component.addListener(InputListener.class, inputListener);
	return (B) this;
    }
    
    
}