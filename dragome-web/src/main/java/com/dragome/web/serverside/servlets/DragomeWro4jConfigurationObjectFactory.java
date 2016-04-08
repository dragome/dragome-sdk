package com.dragome.web.serverside.servlets;

import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.util.ObjectFactory;

public class DragomeWro4jConfigurationObjectFactory implements ObjectFactory<WroConfiguration>
{
	public WroConfiguration create()
	{
		WroConfiguration wroConfiguration= new WroConfiguration();
		//				wroConfiguration.setDisableCache(true);
		wroConfiguration.setResourceWatcherAsync(true);
		wroConfiguration.setResourceWatcherUpdatePeriod(1);
		return wroConfiguration;
	}
}