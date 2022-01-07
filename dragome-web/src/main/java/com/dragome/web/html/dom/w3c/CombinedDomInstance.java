package com.dragome.web.html.dom.w3c;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.cobraparser.html.domimpl.HTMLDocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import com.dragome.commons.AbstractProxyRelatedInvocationHandler;
import com.dragome.commons.ProxyRelatedInvocationHandler;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.helpers.XPathGenerator;

public class CombinedDomInstance
{
	private static Map<Integer, CombinedDomInstance> fromLocal= new HashMap<>();
	private static Map<Integer, CombinedDomInstance> fromRemote= new HashMap<>();

	private Object localInstance;
	private Object remoteInstance;
	private boolean browserReference;
	private HTMLDocumentImpl document;
	private static int rr;
	private static String innerHTML;

	public CombinedDomInstance(Object localInstance, Object remoteInstance, boolean browserReference)
	{
		this.browserReference= browserReference;
		this.setLocalInstance(localInstance);
		this.setRemoteInstance(remoteInstance);
		CombinedDomInstance combinedDomInstance= getFromLocal(localInstance);
		if (combinedDomInstance == null)
			updateMaps(localInstance, remoteInstance, this);

		document= CobraDomHandler.htmlDocumentImpl;
		updateInnerHTML();
	}

	public void updateInnerHTML()
	{
		//		innerHTML= document.getInnerHTML();
	}

	public Object invoke(Method method, Object[] args)
	{
		updateInnerHTML();

		Object result;
		try
		{
			boolean discardReturnValue= !method.getName().equals("createElement") && !method.getName().equals("cloneNode") && !method.getName().equals("createTextNode");
			boolean isBrowserReference= false;
			Object remote= remoteInstance;
			Object remoteResult= null;
			if (remote != null)
			{
				Object invoke2= null;
				List<String> modifierMethods= Arrays.asList("insertBefore", "appendChild", "replaceChild", "removeChild", "setTextContent");
				if (modifierMethods.contains(method.getName()) || method.getReturnType() == void.class || method.getReturnType() == Void.class || !discardReturnValue)
				{
					boolean isAddListenerMethod= method.getName().equals("addEventListener");
					boolean isNotVoidMethod= method.getName().equals("cloneNode") //
							|| method.getName().equals("createElement") //
							|| method.getName().equals("appendChild") //
							|| method.getName().equals("setTextContent")// 
							|| method.getName().equals("removeChild") //
							|| method.getName().equals("insertBefore") //
							|| method.getName().equals("createTextNode") //
							|| isAddListenerMethod//
							|| method.getName().equals("replaceChild");

					if (!Proxy.isProxyClass(remote.getClass()) && isNotVoidMethod)
					{
						if (isAddListenerMethod)
						{
							JsCast.addEventListener((EventTarget) remote, args[0].toString(), (EventListener) args[1]);
						}
						else
						{
							String delegateClassName= JsCast.createDelegateClassName(ElementExtension.class.getName());
							Class<?> class2= Class.forName(delegateClassName);
							Object newInstance= class2.newInstance();

							ScriptHelper.put("instance", remote, this);
							ScriptHelper.put("delegate", newInstance, this);

							Object[] convertArgs= convertArgs(args, false);
							String argsToCall= "";
							for (int i= 0; i < convertArgs.length; i++)
							{
								String string= "$" + i;
								argsToCall+= string;
								if (convertArgs[i] instanceof Node)
									argsToCall+= ".node";

								if (i < convertArgs.length - 1)
									argsToCall+= ",";
								ScriptHelper.put(string, convertArgs[i], this);
							}

							String script= "delegate.node= instance.node." + method.getName() + "(" + argsToCall + ")";
							ScriptHelper.evalNoResult(script, this);
							invoke2= newInstance;
						}
					}
					else
						invoke2= getActualMethod(method, remote).invoke(remote, convertArgs(args, false));

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
			else
				isBrowserReference= true;

			if (result != null && !result.getClass().isPrimitive() && !(result instanceof Boolean) && !(result instanceof String) && !(result instanceof Number))
				new CombinedDomInstance(result, remoteResult, isBrowserReference);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | InstantiationException e)
		{
			throw new RuntimeException(e);
		}
		return result;
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

	private Object createProxyForminvokeRemoteWithNoInstance(Object localInstance2)
	{
		AbstractProxyRelatedInvocationHandler h= new AbstractProxyRelatedInvocationHandler()
		{
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
			{
				boolean realResult= method.getName().equals("cloneNode");

				if (method.getName().equals("hashCode"))
					return 1;

				if (method.getName().equals("isSameNode"))
					return false;

				if (Number.class.isAssignableFrom(method.getReturnType()) || method.getReturnType() == int.class || method.getReturnType() == float.class || method.getReturnType() == long.class || method.getReturnType() == short.class || method.getReturnType() == double.class)
					return 0;
				if (Boolean.class.isAssignableFrom(method.getReturnType()) || method.getReturnType() == boolean.class)
					return true;
				if (String.class.isAssignableFrom(method.getReturnType()))
					return "";

				if (method.getName().equals("addEventListener"))
				{
					String script= createElementAccessorScript(localInstance2);
					ElementExtension eval= ScriptHelper.evalCasting(script, ElementExtension.class, this);
					JsCast.addEventListener((EventTarget) eval, args[0].toString(), (EventListener) args[1]);
					return null;
				}
				else
				{
					String script= executeRemote(localInstance2, method, args);

					if (script.isEmpty())
						return null;

					if (realResult)
					{
						String delegateClassName= JsCast.createDelegateClassName(ElementExtension.class.getName());
						Class<?> class2= Class.forName(delegateClassName);
						Object newInstance= class2.newInstance();

						ScriptHelper.put("instance", script, this);
						ScriptHelper.put("delegate", newInstance, this);
						ScriptHelper.evalNoResult("delegate.node= eval(instance)", this);

						return newInstance;
					}
					else
					{
						String script2= script;
						ScriptHelper.evalNoResult(script2, this);
						return null;
					}
				}
			}

			public String executeRemote(Object localInstance2, Method method, Object[] args)
			{
				String script= "";
				try
				{
					if (localInstance2 instanceof Node && ((Node) localInstance2).getParentNode() != null)
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
										Node element1= (Node) object;
										Node remoteParent1= findTopParent(element1);
										String parentVariableName1= createParentVariableName(remoteParent1);
										script1= createScript(element1, parentVariableName1);
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
				}
				catch (Exception e)
				{
					e.printStackTrace();
					script= "";
				}
				return script;
			}

			public String createElementAccessorScript(Object localInstance2)
			{
				String script;
				Element element= (Element) localInstance2;
				Node remoteParent= findTopParent(element);
				String parentVariableName= createParentVariableName(remoteParent);
				script= createScript(element, parentVariableName);
				return script;
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

	//	public Object findByXpath(Object object)
	//	{
	//		Object result= object;
	//		Element element= (Element) fromLocal.get(object);
	//
	//		if (element != null)
	//			return element;
	//
	//		if (object instanceof Node)
	//		{
	//
	//			String xPath= XPathGenerator.getFullXPath((Node) object);
	//
	//			if (xPath != null && !xPath.trim().isEmpty())
	//			{
	//
	//				ScriptHelper.put("xpathExpression", xPath, this);
	//				ElementExtension foundElement= ScriptHelper.evalCasting("document.evaluate(xpathExpression, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue", ElementExtension.class, this);
	//
	//				if (foundElement != null)
	//				{
	//					fromLocal.put(object, foundElement);
	//					result= foundElement;
	//				}
	//			}
	//		}
	//
	//		return result;
	//	}

	//	protected Object[] convertToRemote(Object[] args)
	//	{
	//		if (args != null)
	//		{
	//			Object[] resultingArray= Arrays.copyOf(args, args.length);
	//			for (int i= 0; i < args.length; i++)
	//				resultingArray[i]= findByXpath(args[i]);
	//			return resultingArray;
	//		}
	//		else
	//			return args;
	//	}

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
