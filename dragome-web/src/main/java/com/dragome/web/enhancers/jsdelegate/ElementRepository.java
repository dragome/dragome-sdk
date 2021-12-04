package com.dragome.web.enhancers.jsdelegate;

import org.w3c.dom.Element;

import com.dragome.annotations.ServiceImplementation;

@ServiceImplementation(value= ElementRepositoryImpl.class)
public interface ElementRepository
{
	ElementData getElementData(Object element);
	Element cloneElement(Element node);
}
