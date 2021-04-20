/**
 * ﻿Original work: copyright 1999-2004 The Apache Software Foundation
 * (http://www.apache.org/)
 *
 * This project is based on the work licensed to the Apache Software
 * Foundation (ASF) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Modified work: copyright 2013-2019 Valery Silaev (http://vsilaev.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.callbackevictor.serverside.javaflow.providers.asmx;

import net.tascalate.asmx.Type;
import net.tascalate.asmx.tree.analysis.BasicValue;
import net.tascalate.asmx.tree.analysis.SimpleVerifier;

class FastClassVerifier extends SimpleVerifier {
    private final ClassHierarchy classHierarchy;
    
    FastClassVerifier(ClassHierarchy classHierarchy) {
        super(AsmVersion.CURRENT, null, null, null, false);
        this.classHierarchy = classHierarchy;
    }
    
    @Override
    protected boolean isAssignableFrom(Type t, Type u) {
        if (t.equals(u)) {
            return true;
        }
        // Null is assignable to any reference type
        if ("Lnull;".equals(u.getDescriptor()) && t.getSort() >= Type.ARRAY) {
            return true;
        }
        Type et, eu;
        if (t.getSort() == Type.ARRAY) {
            if (u.getSort() != Type.ARRAY ) {
                return false;
            }
            et = t.getElementType();
            eu = u.getElementType();
            int dt = t.getDimensions();
            int du = u.getDimensions();
            boolean isObject = et.equals(BasicValue.REFERENCE_VALUE.getType());
            // u must be an array of equals dimension or bigger dimension if t is Object
            if (dt == du || dt < du && isObject) {
                // Ok
            } else {
                return false;
            }
        } else {
            et = t; 
            eu = u;
        }
        /*
        Type commonType = classHierarchy.getCommonSuperType(et, eu);
        */
        // isAssignableFrom(Number, Integer) => getCommonSuperType(Integer, Number) == Number        
        // Use ClassHierarchy.isSubclass biased behavior (for performance)
        Type commonType = classHierarchy.getCommonSuperType(eu, et);
        return commonType.equals(et);

    }
    
    @Override
    public BasicValue merge(BasicValue v, BasicValue w) {
        if (!v.equals(w)) {
            Type t = v.getType();
            Type u = w.getType();
            int tsort = t == null ? -1 : t.getSort();
            if (tsort == Type.OBJECT || tsort == Type.ARRAY) {
                int usort = u == null ? -1 : u.getSort();
                if (usort == Type.OBJECT || usort == Type.ARRAY) {
                    if ("Lnull;".equals(t.getDescriptor())) {
                        return w;
                    }
                    if ("Lnull;".equals(u.getDescriptor())) {
                        return v;
                    }
                    if (isAssignableFrom(t, u)) {
                        return v;
                    }
                    if (isAssignableFrom(u, t)) {
                        return w;
                    }
                    return new BasicValue(classHierarchy.getCommonSuperType(t, u));
                }
            }
            return BasicValue.UNINITIALIZED_VALUE;
        }
        return v;
    }

    @Override
    protected Class<?> getClass(Type t) { 
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected boolean isInterface(Type t) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected Type getSuperClass(Type t) {
        throw new UnsupportedOperationException();
    }

}
