package com.dragome.html.dom;

import java.io.Serializable;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.RequestExecutorImpl;
import com.dragome.services.interfaces.AsyncCallback;

public class Timer
{
	public Object setInterval(final Runnable runnable, int milliseconds)
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

		ScriptHelper.put("milliseconds", milliseconds, this);
		ScriptHelper.put("wrappedCallback", wrappedCallback, this);
		ScriptHelper.eval("window.wrappedCallback=wrappedCallback", this);
		return ScriptHelper.eval("setInterval('window.wrappedCallback.$onSuccess___java_lang_Object$void(new String())', milliseconds)", this);
	}

	public void clearInterval(Object interval)
	{
		ScriptHelper.put("interval", interval, this);
		interval= ScriptHelper.eval("clearInterval(interval)", this);
	}

}
