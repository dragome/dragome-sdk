package com.dragome.view;

import com.dragome.services.ServiceLocator;
import com.dragome.services.interfaces.ParametersHandler;
import com.dragome.services.interfaces.ServiceFactory;

public abstract class DefaultVisualActivity implements VisualActivity
{
	protected ServiceFactory serviceFactory= ServiceLocator.getInstance().getServiceFactory();
	protected ParametersHandler parametersHandler= ServiceLocator.getInstance().getParametersHandler();

	public DefaultVisualActivity()
	{
	}

	public void onDestroy()
	{
	}

	public void onPause()
	{
	}

	public void onRestart()
	{
	}

	public void onResume()
	{
	}

	public void onStart()
	{
	}

	public void onStop()
	{
	}

	public void openActivity(VisualActivity visualActivity)
	{
		visualActivity.onCreate();
	}
	
	public void onCreate()
	{
		build();
	}
}