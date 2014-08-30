package com.dragome.forms.bindings.builders;

import com.dragome.model.interfaces.VisualComponent;

public class Case
{
    private Supplier<?> supplier;
    
    public Case()
    {
    }
    
    public Supplier<?> getSupplier()
    {
        return supplier;
    }

    public void setSupplier(Supplier<?> supplier)
    {
        this.supplier= supplier;
    }

    public VisualComponent getComponent()
    {
        return component;
    }

    public void setComponent(VisualComponent component)
    {
        this.component= component;
    }

    private VisualComponent component;

    public Case(Supplier<?> supplier, VisualComponent component)
    {
	this.supplier= supplier;
	this.component= component;
    }

}
