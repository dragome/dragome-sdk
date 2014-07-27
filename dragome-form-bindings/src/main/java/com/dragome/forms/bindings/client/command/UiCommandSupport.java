package com.dragome.forms.bindings.client.command;

import static com.dragome.forms.bindings.client.condition.Conditions.isNot;

import com.dragome.forms.bindings.client.interceptor.Interceptor;
import com.dragome.forms.bindings.client.interceptor.InterceptorChain;
import com.dragome.forms.bindings.client.interceptor.Invocation;
import com.dragome.forms.bindings.client.value.DelegatingValueModel;
import com.dragome.forms.bindings.client.value.ValueModel;
import com.dragome.forms.bindings.extra.user.client.Command;

/**
 * Base class for all UiCommands that defines the common state (enabled) and methods
 * for configuring it.
 */
public abstract class UiCommandSupport implements UiCommand
{
	private DelegatingValueModel<Boolean> enabledModel= new DelegatingValueModel<Boolean>(true);
	protected InterceptorChain interceptorChain= new InterceptorChain();

	public ValueModel<Boolean> enabled()
	{
		return enabledModel;
	}

	/**
	 * Configures when this command should be enabled.  Multiple calls to this method will replace
	 * the existing value with the new one.
	 * <p/>
	 * This command will be referenced by the specified value model (think memory leak). So if the
	 * specified model is long lived then you'll need to invoke enabledWhen(null) to clear the
	 * reference.
	 *
	 * @param enabled a value model representing the enabled state of this command or null to remove
	 *                the enabledWhen condition.
	 */
	public void enableWhen(ValueModel<Boolean> enabled)
	{
		enabledModel.setDelegate(enabled);
	}

	/**
	 * Configures when this command should be disabled.  This method is equivalent to calling
	 * {@link #enableWhen(com.pietschy.gwt.pectin.client.value.ValueModel) enableWhen(isNot(disabled))}.
	 * <p/>
	 * See {@link #enableWhen(com.pietschy.gwt.pectin.client.value.ValueModel)} for information on memory
	 * leaks.
	 *
	 * @param disabled a value model representing the disabled state of this command.
	 */
	public void disableWhen(ValueModel<Boolean> disabled)
	{
		enableWhen(isNot(disabled));
	}

	public final void interceptUsing(Interceptor interceptor)
	{
		interceptorChain.addInterceptor(interceptor);
	}

	/**
	 * Allows subclasses to intercept the execution.  This method will run before any
	 * other interceptors.
	 * <p/>
	 * The command <b>will only execute</b> when <code>invocation.proceed()</code> or <code>invocation.getProceedCommand().execute()</code>.
	 * is called.
	 * <p/>
	 * To cancel the command don't call <code>invocation.proceed()</code> or <code>invocation.getProceedCommand().execute()</code>.
	 * <p/>
	 * <b>NOTE:</b> Calling <code>super.beforeExecute(invocation)</code> is equivalent to simply calling
	 * <code>invocation.proceed()</code>.
	 * <p/>
	 * Examples:
	 * <pre>
	 * protected void beforeExecute(Invocation invocation)
	 * {
	 *    // only proceed if the model is valid.
	 *    if (model.validate())
	 *    {
	 *       invocation.proceed();
	 *    }
	 * }
	 * </pre>
	 *
	 * @param invocation the invocation chain.
	 * @see #interceptUsing(com.pietschy.gwt.pectin.client.interceptor.Interceptor)
	 */
	protected void intercept(Invocation invocation)
	{
		invocation.proceed();
	}

	protected final void runWithInterceptors(Command command)
	{
		// create the interceptor chain with our execution at the end of it.  By passing it to
		// before starting we guarantee that beforeStarting has first shot and intercepting it.
		intercept(interceptorChain.buildInvocationChain(command));
	}
}
