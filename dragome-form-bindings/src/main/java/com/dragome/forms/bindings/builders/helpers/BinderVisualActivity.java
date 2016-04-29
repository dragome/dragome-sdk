package com.dragome.forms.bindings.builders.helpers;

import com.dragome.guia.GuiaVisualActivity;

public abstract class BinderVisualActivity extends GuiaVisualActivity
{
	protected void updateMainPanel()
	{
		super.updateMainPanel();
		BinderHelper.start(mainPanel);
	}
}
