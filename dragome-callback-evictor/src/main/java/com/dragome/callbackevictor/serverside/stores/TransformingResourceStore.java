/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.callbackevictor.serverside.stores;

import org.apache.commons.jci.stores.ResourceStore;

import com.dragome.callbackevictor.serverside.bytecode.transformation.ResourceTransformer;

public class TransformingResourceStore implements ResourceStore {

    private final ResourceStore store;
    private final ResourceTransformer[] transformers;
    
    public TransformingResourceStore( final ResourceStore pStore, final ResourceTransformer[] pTransformers) {
        store = pStore;
        transformers = pTransformers;
    }
    
    public byte[] read(String resourceName) {
        return store.read(resourceName);
    }
    public void remove(String resourceName) {
        store.remove(resourceName);
    }
    public void write(String resourceName, byte[] resourceData) {
        byte[] out = resourceData;
        
        // FIXME: make the matching configurable
        //if (resourceName.endsWith(".class")) {
            byte[] in = resourceData;
            
            for(int i=0; i<transformers.length; i++) {
                out = transformers[i].transform(in);
                in = out;
            }
        //}

        store.write(resourceName, out);
    }
    
    public String toString() {
        return store.toString();
    }
}
