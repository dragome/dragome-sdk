package com.dragome.html.dom;

import java.io.Serializable;

import org.w3c.dom.Element;
import org.w3c.dom.events.EventListener;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.dispatcher.EventDispatcherImpl;
import com.dragome.helpers.DragomeEntityManager;
import com.dragome.services.RequestExecutorImpl;
import com.dragome.services.ServiceLocator;
import com.dragome.services.interfaces.AsyncCallback;

public class Window
{
	private static Window instance;

	public Window()
	{
	}

	public static Window getInstance()
	{
		if (instance == null)
			instance= new Window();

		return instance;
	}

	public int getClientWidth()
	{
		return ScriptHelper.evalInt("window.innerWidth", this);
	}

	public int getClientHeight()
	{
		return ScriptHelper.evalInt("window.innerHeight", this);
	}

	public void requestAnimationFrame(Runnable aRunnable)
	{
		Runnable runnableForDebugging= wrapRunnableForDebugging(aRunnable);
		ScriptHelper.put("ra", runnableForDebugging, this);
		ScriptHelper.evalNoResult("requestAnimationFrame(function(time) {ra.$run$void();})", this);
	}

	public void addEventListener(EventListener aEventListener, String... aEvent)
	{
		Element theElement= ServiceLocator.getInstance().getDomHandler().getElementBySelector("body");
		EventDispatcherImpl.setEventListener(theElement, aEventListener, aEvent);
	}

	public void onResize(Runnable aRunnable)
	{
		Runnable runnableForDebugging= wrapRunnableForDebugging(aRunnable);
		ScriptHelper.put("ra", runnableForDebugging, this);
		ScriptHelper.evalNoResult("window.addEventListener('resize', function() {ra.$run$void();})", this);
	}

	public void onHashChange(Runnable aRunnable)
	{
		Runnable runnableForDebugging= wrapRunnableForDebugging(aRunnable);
		ScriptHelper.put("ra", runnableForDebugging, this);
		ScriptHelper.evalNoResult("window.onhashchange= function() {ra.$run$void();}", this);
	}

	public static Runnable wrapRunnableForDebugging(final Runnable runnable)
	{
		AsyncCallback<String> asyncCallback= new AsyncCallback<String>()
		{
			public void onSuccess(String result)
			{
				runnable.run();
			}

			public void onError()
			{
			}
		};

		AsyncCallback<String> wrappedCallback= RequestExecutorImpl.wrapCallback(Serializable.class, asyncCallback);

		Runnable runnable2= new Runnable()
		{
			public void run()
			{
				ScriptHelper.eval("window['callback_'+this.javaId].$onSuccess___java_lang_Object$void(new String())", this);
			}
		};

		ScriptHelper.put("callback", wrappedCallback, null);
		ScriptHelper.put("javaId", DragomeEntityManager.getEntityId(runnable2), null);
		ScriptHelper.put("runnable2", runnable2, null);
		ScriptHelper.eval("runnable2.javaId= javaId", null);
		ScriptHelper.eval("window['callback_'+javaId]= callback", null);
		return runnable2;
	}
}