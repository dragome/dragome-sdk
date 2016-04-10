package com.dragome.web.serverside.servlets;

import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

public class DragomeWroModelFactory implements WroModelFactory
{
	public WroModel create()
	{
		Group dragomeGroup= new Group("dragome");
		dragomeGroup.addResource(Resource.create("/dragome-resources/js/hashtable.js", ResourceType.JS));
		dragomeGroup.addResource(Resource.create("/dragome-resources/js/deflate.js", ResourceType.JS));
		dragomeGroup.addResource(Resource.create("/dragome-resources/js/helpers.js", ResourceType.JS));
		dragomeGroup.addResource(Resource.create("/dragome-resources/js/string.js", ResourceType.JS));
		dragomeGroup.addResource(Resource.create("/dragome-resources/js/qx-oo-5.0.1.min.js", ResourceType.JS));
		dragomeGroup.addResource(Resource.create("/compiled-js/webapp.js", ResourceType.JS));
		dragomeGroup.addResource(Resource.create("/dragome-resources/css/dragome.css", ResourceType.CSS));

		Group compiledGroup= new Group("compiled");
		compiledGroup.addResource(Resource.create("/compiled-js/webapp.js", ResourceType.JS));

		WroModel wroModel= new WroModel();
		wroModel.addGroup(dragomeGroup);
		wroModel.addGroup(compiledGroup);

		return wroModel;
	}
	public void destroy()
	{
	}
}