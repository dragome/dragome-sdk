/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.model.interfaces;

import java.util.EventListener;

public interface EventProducer
{
	public <T extends EventListener> boolean hasListener(Class<T> aType);
	public <T extends EventListener> void removeListener(Class<T> aType, T aListener);
	public <T extends EventListener> void addListener(Class<T> aType, T aListener);
	public <T extends EventListener> T getListener(Class<T> aType);
	public void suspendListening(Class<? extends EventListener> aType);
	public void continueListening(Class<? extends EventListener> aType);
}
