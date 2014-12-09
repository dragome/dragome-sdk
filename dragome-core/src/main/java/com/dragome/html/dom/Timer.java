package com.dragome.html.dom;

import java.io.Serializable;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.RequestExecutorImpl;
import com.dragome.services.interfaces.AsyncCallback;

public class Timer
{
	private Object id;

	public Timer setInterval(final Runnable runnable, int milliseconds)
	{
		setupTimer(runnable);
		ScriptHelper.put("milliseconds", milliseconds, this);
		id= ScriptHelper.eval("setInterval('window.wrappedCallback.$onSuccess___java_lang_Object$void(new String())', milliseconds)", this);
		return this;
	}

	private void setupTimer(final Runnable runnable)
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

		ScriptHelper.put("wrappedCallback", wrappedCallback, this);
		ScriptHelper.eval("window.wrappedCallback=wrappedCallback", this);
	}

	public Timer setTimeout(final Runnable runnable, int milliseconds)
	{
		setupTimer(runnable);
		ScriptHelper.put("milliseconds", milliseconds, this);
		id= ScriptHelper.eval("setTimeout('window.wrappedCallback.$onSuccess___java_lang_Object$void(new String())', milliseconds)", this);
		return this;
	}

	public void clearInterval()
	{
		ScriptHelper.put("interval", id, this);
		ScriptHelper.eval("clearInterval(interval)", this);
	}

}
