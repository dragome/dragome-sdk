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
package com.dragome.services.interfaces;

import java.util.Map;

public interface RequestExecutor
{
	public String executeSynchronousRequest(String url, Map<String, String> parameters);
	public String executeFixedSynchronousRequest(String url, Map<String, String> parameters);
	public String executeAsynchronousRequest(String url, Map<String, String> parameters, AsyncCallback<String> asyncCallback);
	public void executeCrossDomainAsynchronousRequest(String url, Map<String, String> parameters, AsyncCallback<String> asyncCallback);
}
