/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dragome.forms.bindings.extra.core.client.impl;

import com.dragome.forms.bindings.extra.core.client.Scheduler;

/**
 * This is used by Scheduler to collaborate with Impl in order to have
 * FinallyCommands executed.
 */
public class SchedulerImpl extends Scheduler
{

	public static Scheduler INSTANCE;

	@Override
	public void scheduleDeferred(ScheduledCommand cmd)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void scheduleEntry(RepeatingCommand cmd)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void scheduleEntry(ScheduledCommand cmd)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void scheduleFinally(RepeatingCommand cmd)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void scheduleFinally(ScheduledCommand cmd)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void scheduleFixedDelay(RepeatingCommand cmd, int delayMs)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void scheduleFixedPeriod(RepeatingCommand cmd, int delayMs)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void scheduleIncremental(RepeatingCommand cmd)
	{
		// TODO Auto-generated method stub

	}
}
