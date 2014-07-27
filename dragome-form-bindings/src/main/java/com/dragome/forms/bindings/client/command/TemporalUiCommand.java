package com.dragome.forms.bindings.client.command;

import com.dragome.forms.bindings.client.value.ValueModel;

/**
 * TemporalUiCommand represent a command whose execution is not synchronous (i.e. the
 * command doesn't complete until sometime after execute returns).  TemporalUiCommands
 * provide a {@link com.pietschy.gwt.pectin.client.value.ValueModel ValueModel&lt;Boolean&gt;} that
 * holds the active state of the command.
 * <p>
 * The two main subclasses are {@link AbstractAsyncUiCommand} and
 * {@link IncrementalUiCommand}.
 */
public interface TemporalUiCommand extends UiCommand
{
	/**
	 * Returns a value model that is true while the command is executing.
	 *
	 * @return a value model that is true while the command is executing.
	 */
	ValueModel<Boolean> active();
}
