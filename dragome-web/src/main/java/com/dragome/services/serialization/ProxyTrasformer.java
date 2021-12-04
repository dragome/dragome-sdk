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
package com.dragome.services.serialization;

import org.mockito.cglib.proxy.Proxy;

import com.dragome.commons.ProxyRelatedInvocationHandler;

import flexjson.transformer.ObjectTransformer;

public class ProxyTrasformer extends ObjectTransformer
{
	public void transform(Object object)
	{
		ProxyRelatedInvocationHandler invocationHandler= (ProxyRelatedInvocationHandler) Proxy.getInvocationHandler(object);
		Object proxy= invocationHandler.getProxy();
		super.transform(proxy);
	}
}
