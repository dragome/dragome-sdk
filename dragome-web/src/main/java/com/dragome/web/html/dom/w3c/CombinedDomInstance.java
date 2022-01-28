package com.dragome.web.html.dom.w3c;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cobraparser.html.domimpl.HTMLDocumentImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLSelectElement;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.commons.ProxyRelatedInvocationHandler;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.helpers.XPathGenerator;

public class CombinedDomInstance
{
	private static Map<Integer, CombinedDomInstance> fromLocal= new HashMap<>();
	private static Map<Integer, CombinedDomInstance> fromRemote= new HashMap<>();

	private Object localInstance;
	private Object remoteInstance;
	private HTMLDocumentImpl document;
	private static String innerHTML;
	List<String> modifierMethods= Arrays.asList("setInnerHTML", "insertBefore", "appendChild", "replaceChild", "removeChild", "setTextContent", "setAttribute", "removeAttribute");
	private static int refresher;

	public static void reset()
	{
		fromLocal.clear();
		fromRemote.clear();
	}

	public CombinedDomInstance(Object localInstance, Object remoteInstance)
	{
		this.setLocalInstance(localInstance);
		this.setRemoteInstance(remoteInstance);
		CombinedDomInstance combinedDomInstance= getFromLocal(localInstance);
		if (combinedDomInstance == null)
			updateMaps(localInstance, remoteInstance, this);

		document= CobraDomHandler.htmlDocumentImpl;
	}

	public void updateInnerHTML()
	{
		innerHTML= document.getInnerHTML();
	}

	public Object invoke(Method method, Object[] args)
	{
		Object result;
		try
		{
			boolean discardReturnValue= !method.getName().equals("createElement") && !method.getName().equals("cloneNode") && !method.getName().equals("createTextNode");
			Object remote= remoteInstance;
			Object remoteResult= null;
			if (remote != null)
			{
				Object invoke2= null;
				boolean modifier= modifierMethods.contains(method.getName());
				if (modifier || method.getReturnType() == void.class || method.getReturnType() == Void.class || !discardReturnValue)
				{
					boolean isProxyRemote= Proxy.isProxyClass(remote.getClass());

					if (isAddingEventListener(method))
					{
						EventTarget eventTarget= (EventTarget) remote;

						if (isProxyRemote)
							eventTarget= (EventTarget) invokeAndCreateLocalInstance(createElementAccessorScript(localInstance), method, true);

						addEventListenerWrapper(eventTarget, args[0].toString(), (EventListener) args[1]);
					}
					else
					{
						if (modifier || !discardReturnValue)
						{
							if (!method.getName().equals("setInnerHTML"))
								invoke2= invokeAndCreateLocalInstance(createRemoteInstanceGetterScript(localInstance, method, args), method, true);
							else
								invoke2= getActualMethod(method, remote).invoke(remote, convertArgs(args, false));
						}
					}

					if (!discardReturnValue && invoke2 instanceof Element && !(invoke2 instanceof ElementExtension))
						invoke2= new BrowserDomHandler().castTo(invoke2, ElementExtension.class, this);
				}
				remoteResult= invoke2;
			}
			else
			{
				System.out.println("null!!");
			}

			result= getActualMethod(method, localInstance).invoke(localInstance, convertArgs(args, true));

			if (discardReturnValue)
				remoteResult= null;

			if (result != null && !result.getClass().isPrimitive() && !(result instanceof Boolean) && !(result instanceof String) && !(result instanceof Number))
				new CombinedDomInstance(result, remoteResult);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
		return result;
	}

	public boolean isAddingEventListener(Method method)
	{
		return method.getName().equals("addEventListener");
	}

	public Object invokeAndCreateLocalInstance(String localInstanceAssignmentScript, Method method, boolean realResult)
	{
		try
		{
			if (realResult)
			{
				Object newInstance= Class.forName(JsCast.createDelegateClassName(ElementExtension.class.getName())).newInstance();

				ScriptHelper.put("delegate", newInstance, this);
				String finalScript= "delegate.node= " + localInstanceAssignmentScript;
				ScriptHelper.evalNoResult(finalScript, this);
				return newInstance;
			}
			else
			{
				String script2= localInstanceAssignmentScript;
				if (modifierMethods.contains(method.getName()))
				{
					if (false && (refresher++ % 1) == 0)
						ScriptHelper.eval(script2, this);
					else
						ScriptHelper.evalNoResult(script2, this);
				}
				else
				{
					ScriptHelper.evalNoResult(script2, this);
				}
				return null;
			}
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void updateMaps(Object local, Object remote, CombinedDomInstance combinedDomInstance)
	{
		fromLocal.put(System.identityHashCode(local), combinedDomInstance);
		if (remote == null)
		{
			remote= createProxyForminvokeRemoteWithNoInstance(local);
			combinedDomInstance.setRemoteInstance(remote);
		}
		fromRemote.put(System.identityHashCode(remote), combinedDomInstance);
	}

	public String createElementAccessorScript(Object aLocalInstance)
	{
		if (aLocalInstance instanceof HTMLDocument)
			return "document";
		else
		{
			Element element= (Element) aLocalInstance;
			Node remoteParent= findTopParent(element);
			String parentVariableName= createParentVariableName(remoteParent);
			return createScript(element, parentVariableName);
		}
	}

	public String createRemoteInstanceGetterScript(Object localInstance2, Method method, Object[] args)
	{
		String script= "";
		try
		{
			if (!method.getName().equals("getNodeName") && !method.getName().equals("getTagName"))
			{
				script= createElementAccessorScript(localInstance2);

				script+= "." + adaptNameToJs(method);

				Object[] convertArgs= convertArgs(args, false);
				if (args != null)
				{
					boolean isSetterAsProperty= method.getName().startsWith("set") && args.length == 1;
					if (isSetterAsProperty)
						script+= "= ";
					else
						script+= "(";
					for (int i= 0; i < convertArgs.length; i++)
					{
						String script1;
						Object object= convertArgs[i];
						if (object instanceof Node)
						{
							script1= createElementAccessorScript(object);

							if (method.getName().equals("replaceChild") && i == 1)
							{
								Object newInstance= invokeAndCreateLocalInstance(script1, method, true);

								if (args[i] != null && Proxy.isProxyClass(args[i].getClass()))
								{
									ProxyRelatedInvocationHandler invocationHandler= (ProxyRelatedInvocationHandler) Proxy.getInvocationHandler(args[i]);
									CombinedDomInstance combinedDomInstance= (CombinedDomInstance) invocationHandler.getProxy();
									combinedDomInstance.setRemoteInstance(newInstance);
									updateMaps(combinedDomInstance.getLocalInstance(), combinedDomInstance.getRemoteInstance(), combinedDomInstance);
								}
							}
						}
						else
						{
							String replace= "";
							if (object != null)
								replace= object.toString().replace("'", "\\\\'");

							script1= "'" + replace + "'";
						}

						script+= script1;
						if (i < convertArgs.length - 1)
							script+= ", ";
					}

					if (!isSetterAsProperty)
						script+= ") ";
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			script= "";
		}
		return script;
	}

	public void addEventListenerWrapper(EventTarget eventTarget, String type, EventListener eventListener)
	{
		JsCast.addEventListener(eventTarget, type, new EventListener()
		{
			public void handleEvent(Event evt)
			{
				//				updateInnerHTML();
				String type= evt.getType();
				EventTarget target= evt.getTarget();
				if ("change".equals(type))
				{
					if (localInstance instanceof HTMLSelectElement)
					{
						HTMLSelectElement remoteHtmlSelectElement= JsCast.castTo(target, HTMLSelectElement.class);
						HTMLSelectElement htmlSelectElement= (HTMLSelectElement) localInstance;

						int selectedIndex= remoteHtmlSelectElement.getSelectedIndex();
						htmlSelectElement.setSelectedIndex(selectedIndex);
					}
					else if (localInstance instanceof HTMLInputElement)
					{
						HTMLInputElement htmlInputElement= (HTMLInputElement) localInstance;
						HTMLInputElement remoteHtmlInputElement= JsCast.castTo(target, HTMLInputElement.class);

						String value= remoteHtmlInputElement.getValue();
						htmlInputElement.setValue(value);
					}
				}
				else if ("click".equals(type))
				{
					if (localInstance instanceof HTMLInputElement)
					{
						HTMLInputElement htmlInputElement= (HTMLInputElement) localInstance;
						HTMLInputElement remoteHtmlInputElement= JsCast.castTo(target, HTMLInputElement.class);

						boolean checked= remoteHtmlInputElement.getChecked();
						if (checked)
							htmlInputElement.setAttribute("checked", "checked");
						else
							htmlInputElement.removeAttribute("checked");

						htmlInputElement.setChecked(checked);
					}
				}
				eventListener.handleEvent(evt);
				ScriptHelper.eval("document", this);
			}
		});
	}

	private Object createProxyForminvokeRemoteWithNoInstance(Object localInstance2)
	{
		AbstractProxyRelatedInvocationHandler h= new AbstractProxyRelatedInvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				if (method.getName().equals("hashCode") || Number.class.isAssignableFrom(method.getReturnType()) || method.getReturnType() == int.class || method.getReturnType() == float.class || method.getReturnType() == long.class || method.getReturnType() == short.class || method.getReturnType() == double.class)
					return 0;
				else if (method.getName().equals("isSameNode") || Boolean.class.isAssignableFrom(method.getReturnType()) || method.getReturnType() == boolean.class)
					return true;
				else if (String.class.isAssignableFrom(method.getReturnType()))
					return "";
				else if (isAddingEventListener(method))
				{
					Object createLocalInstance= invokeAndCreateLocalInstance(createElementAccessorScript(localInstance2), method, true);
					addEventListenerWrapper((EventTarget) createLocalInstance, args[0].toString(), (EventListener) args[1]);
					return null;
				}
				else
				{
					boolean realResult= method.getName().equals("cloneNode");
					return invokeAndCreateLocalInstance(createRemoteInstanceGetterScript(localInstance2, method, args), method, realResult);
				}
			}

		};

		h.setProxy(this);
		return Proxy.newProxyInstance(getClass().getClassLoader(), this.getProxyInterfaces(localInstance), h);
	}

	public Node findTopParent(Node node)
	{
		Node localNode= getLocalInstanceOf(node);
		Node topParent= getTopParent(localNode);

		Node remoteParent= (Node) getFromLocal(topParent).getRemoteInstance();
		return remoteParent;
	}

	private Node getLocalInstanceOf(Node node)
	{
		CombinedDomInstance combinedDomInstance= null;

		if (Proxy.isProxyClass(node.getClass()))
			combinedDomInstance= (CombinedDomInstance) ((ProxyRelatedInvocationHandler) Proxy.getInvocationHandler(node)).getProxy();
		else
		{
			CombinedDomInstance fromRemote2= getFromRemote(node);
			if (fromRemote2 != null)
				combinedDomInstance= fromRemote2;

			CombinedDomInstance fromLocal= getFromLocal(node);
			if (fromLocal != null)
				combinedDomInstance= fromLocal;
		}

		if (combinedDomInstance == null)
			System.out.println("ninguno?");

		return (Node) combinedDomInstance.getLocalInstance();
	}

	public String createScript(Node element, String parentVariable)
	{
		element= getLocalInstanceOf(element);
		String xPath= XPathGenerator.getFullXPath(element);

		if (element.getParentNode() == null || "".equals(xPath))
			xPath= "/";

		if (!xPath.startsWith("HTML"))
			xPath= xPath.indexOf("/") > 0 ? xPath.substring(xPath.indexOf("/") + 1) : xPath;

		String script= "";
		if (xPath != null && !xPath.trim().isEmpty())
			script= "byXpath(\"" + parentVariable + "\", \"" + xPath + "\")";
		return script;
	}

	public String createParentVariableName(Node remoteParent)
	{
		return "" + System.identityHashCode(remoteParent);
	}

	private String adaptNameToJs(Method method)
	{
		String name= method.getName();

		boolean isGetter= name.startsWith("get") && method.getParameterCount() == 0;
		boolean isSetter= name.startsWith("set") && method.getParameterCount() == 1;

		if (isGetter || isSetter)
			return name.toLowerCase().charAt(3) + name.substring(4);
		else if (name.startsWith("is") && method.getParameterCount() == 0)
			return name.toLowerCase().charAt(2) + name.substring(3);

		return name;
	}

	private Node getTopParent(Node element)
	{
		return element.getParentNode() != null ? getTopParent(element.getParentNode()) : element;
	}

	public static Method getActualMethod(Method method, Object remote) throws NoSuchMethodException
	{
		return remote.getClass().getMethod(method.getName(), method.getParameterTypes());
	}

	public static Class<?>[] getProxyInterfaces(Object localInstance)
	{
		Class<?>[] interfaces;
		List<Class<?>> interfacesList= getInterfaces(localInstance);
		interfaces= interfacesList.toArray(new Class[0]);
		return interfaces;
	}

	public static List<Class<?>> getInterfaces(Object localInstance)
	{
		Class<?>[] interfaces= new Class<?>[] {};
		Class<?>[] interfaces2= localInstance.getClass().getInterfaces();
		List<Class<?>> interfacesList= new ArrayList<>();
		interfacesList.addAll(Arrays.asList(interfaces2));
		if (localInstance instanceof Element)
		{
			interfaces= new Class[] { Element.class, ElementExtension.class, EventTarget.class };
			interfacesList.addAll(Arrays.asList(interfaces));
		}
		return interfacesList;
	}

	protected Object[] convertArgs(Object[] args, boolean toLocal)
	{
		if (args != null)
		{
			Object[] resultingArray= Arrays.copyOf(args, args.length);
			for (int i= 0; i < args.length; i++)
			{
				if (args[i] != null && Proxy.isProxyClass(args[i].getClass()))
				{
					ProxyRelatedInvocationHandler invocationHandler= (ProxyRelatedInvocationHandler) Proxy.getInvocationHandler(args[i]);
					CombinedDomInstance combinedDomInstance= (CombinedDomInstance) invocationHandler.getProxy();
					resultingArray[i]= toLocal ? combinedDomInstance.getLocalInstance() : combinedDomInstance.getRemoteInstance();
				}
			}
			return resultingArray;
		}
		else
			return args;
	}

	public Object getLocalInstance()
	{
		return localInstance;
	}

	public void setLocalInstance(Object localInstance)
	{
		this.localInstance= localInstance;
	}

	public Object getRemoteInstance()
	{
		return remoteInstance;
	}

	public void setRemoteInstance(Object remoteInstance)
	{
		this.remoteInstance= remoteInstance;
	}

	public static CombinedDomInstance getFromLocal(Object value)
	{
		int identityHashCode= System.identityHashCode(value);
		return fromLocal.get(identityHashCode);
	}

	public static CombinedDomInstance getFromRemote(Object value)
	{
		int identityHashCode= System.identityHashCode(value);
		return fromRemote.get(identityHashCode);
	}

}
