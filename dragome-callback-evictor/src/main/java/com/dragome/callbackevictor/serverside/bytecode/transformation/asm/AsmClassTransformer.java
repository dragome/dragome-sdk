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
package com.dragome.callbackevictor.serverside.bytecode.transformation.asm;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import com.dragome.callbackevictor.serverside.bytecode.transformation.ResourceTransformer;
import com.dragome.compiler.invokedynamic.serverside.ClassWriterExtension;
import com.dragome.compiler.invokedynamic.serverside.InvokeDynamicBackporter;

public final class AsmClassTransformer implements ResourceTransformer {

    public byte[] transform(final InputStream is) throws IOException {
        return transform(new ClassReader(is));
    }

    public byte[] transform(final byte[] original) {
        return transform(new ClassReader(original));
    }

    private byte[] transform(final ClassReader cr) {
        final ClassWriter cw = new ClassWriterExtension(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        
        
        // print bytecode before transformation
        // cr.accept(new TraceClassVisitor(new ContinuationClassAdapter(this, cw), new PrintWriter(System.out)), false);

        cr.accept(
            new ContinuationClassAdapter(
            decorateClassVisitor(cw, true, null/* System.err */)
            ), 
            0);

        final byte[] bytecode = cw.toByteArray();

        // CheckClassAdapter.verify(new ClassReader(bytecode), true);
        // new ClassReader(bytecode).accept(new ASMifierClassVisitor(new PrintWriter(System.err)), false);
        return bytecode;
    }

    private ClassVisitor decorateClassVisitor(ClassVisitor visitor, final boolean check, final PrintStream dumpStream) {
        if (check) {
            visitor = new CheckClassAdapter(visitor);
            if (null != CHECK_DATA_FLOW) {
                try {
                    // Currently CheckMethodAdapter throws error, so suppress flow checks
                    CHECK_DATA_FLOW.set(visitor, Boolean.FALSE);
                } catch (final IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        if (null != dumpStream) {
            visitor = new TraceClassVisitor(visitor, new PrintWriter(dumpStream));
        }

        return visitor;
    }

    final private static Field CHECK_DATA_FLOW;

    static {
        Field checkDataFlow = null;
        try {
            checkDataFlow = CheckClassAdapter.class.getDeclaredField("checkDataFlow");
            checkDataFlow.setAccessible(true);
        } catch (final NoSuchFieldException ex) {
            // Normal, the field is available only since ASM 3.2
        }

        CHECK_DATA_FLOW = checkDataFlow;
    }
}

