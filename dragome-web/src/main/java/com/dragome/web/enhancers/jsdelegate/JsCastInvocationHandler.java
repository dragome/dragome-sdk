package com.dragome.web.enhancers.jsdelegate;

import java.lang.reflect.Method;
import java.util.List;

import org.w3c.dom.Element;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.web.debugging.messages.ServerToClientServiceInvoker;

public class JsCastInvocationHandler extends AbstractProxyRelatedInvocationHandler
{
	private final Object newInstance;
	private String elementId;

	public JsCastInvocationHandler(Object newInstance)
	{
		this.newInstance= newInstance;
		setProxy(newInstance);

	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		if (args != null && args.length > 0)
		{
			List<Object> adaptArgs= ServerToClientServiceInvoker.adaptArgs(args);
			args= adaptArgs.toArray(new Object[0]);
		}
		
		if (method.getName().equals("hashCode"))
			return newInstance.hashCode();
		else if (method.getName().equals("equals"))
			return newInstance.equals(args[0]);
		else
		{
			Element element= (Element) newInstance;
			findElementId(element);

			ElementData elementData= findElementData(newInstance);

			Object result= null;

			if (method.getName().equals("isSameNode"))
			{
				if (args[0] instanceof Element)
				{
					String attribute= ((Element) args[0]).getAttribute("data-element-id");
					if (elementId != null && attribute != null)
						return elementId.equals(attribute);
				}

				result= method.invoke(newInstance, args);
			}
			else if (method.getName().equals("getNodeName"))
				result= elementData.getNodeName();
			else if (method.getName().equals("getTagName"))
				result= elementData.getTagName();
			else if (method.getName().equals("getAttribute"))
				result= elementData.getAttribute((String) args[0]);
			else if (method.getName().equals("setAttribute"))
			{
				elementData.addAttribute((String) args[0], (String) args[1]);
				result= method.invoke(newInstance, args);
			}
			else
				result= method.invoke(newInstance, args);

			return result;
		}
	}

	public ElementData findElementData(Object newInstance2)
	{
		String key= createAttributeKey(elementId);
		ElementData elementData= JsCast.attributes.get(key);
		if (elementData == null)
		{
			elementData= JsCast.elementRepository.getElementData(newInstance2);
			if (elementData != null)
				JsCast.attributes.put(key, elementData);
		}
		else
		{
		}
		return elementData;
	}

	public String createAttributeKey(String key)
	{
		return elementId + ":" + key;
	}

	public void findElementId(Element element)
	{
		if (elementId == null)
		{
			elementId= element.getAttribute("data-element-id");
			if (elementId == null)
			{
				String add= DragomeEntityManager.add(element);
				elementId= add;
				element.setAttribute("data-element-id", add);
			}
		}
		else
		{
			//				System.out.println("encontro id!");
		}
	}
}