package com.dragome.forms.bindings.builders;

import com.dragome.guia.components.interfaces.VisualComponent;

public interface CaseBuilder<T>
{
	VisualComponent build(T caseBuilder);
}