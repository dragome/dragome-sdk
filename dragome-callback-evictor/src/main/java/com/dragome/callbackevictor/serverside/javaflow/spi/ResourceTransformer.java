/**
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
package com.dragome.callbackevictor.serverside.javaflow.spi;

import java.util.Collection;

/**
 * Byte-code transformer that enhances the class files for javaflow.
 *
 * <p>
 * When Continuation.suspend is called, all the methods in the stack frame needs
 * to be enhanced.
 *
 * @author tcurdt
 */
public interface ResourceTransformer {
    byte[] transform(byte[] original);
    byte[] transform(byte[] original, String retransformClass);
    byte[] transform(byte[] original, String... retransformClasses);
    byte[] transform(byte[] original, Collection<String> retransformClasses);
    
    void release();
}
