package com.dragome.forms.bindings.client.interceptor;

import com.dragome.forms.bindings.extra.user.client.Command;


/**
 * This class represents one link in an interceptor chain.  Invocations allow {@link Interceptor}
 * instances to stop the execution of a command.  In order to continue the execution of a command the interceptor
 * must either call {@link #proceed()} or invoke execute on the command returned by {@link #getProceedCommand()}.
 */
public class Invocation
{
	private ProceedCommand proceedCommand;

	protected Invocation(Command operation)
	{
		this.proceedCommand= new ProceedCommand(operation);
	}

	protected Invocation(final Invocation parent, final Interceptor interceptor)
	{
		this.proceedCommand= new ProceedCommand(new Command()
		{
			public void execute()
			{
				interceptor.intercept(parent);
			}
		});
	}

	/**
	 * Causes the current invocation to proceed immediately.
	 */
	public void proceed()
	{
		getProceedCommand().execute();
	}

	/**
	 * Gets a command that when invoked will proceed with the execution.  Failure to invoke execute on
	 * the command will cancel the command.
	 *
	 * @return a command that when invoked will proceed with the execution.
	 */
	public Command getProceedCommand()
	{
		return proceedCommand;
	}

	/**
	 * Ensures that we aren't executed more than once.
	 */
	private class ProceedCommand implements Command
	{
		private boolean executed;
		private Command command;

		private ProceedCommand(Command command)
		{
			this.command= command;
		}

		public void execute()
		{
			if (executed)
			{
				throw new IllegalStateException("Invocation already executed.");
			}
			else
			{
				executed= true;
				command.execute();
			}
		}
	}
}
