package com.dragome.compiler.invokedynamic.serverside;

import org.objectweb.asm.ClassWriter;

import com.dragome.callbackevictor.serverside.bytecode.transformation.asm.ContinuationMethodAnalyzer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public final class ClassWriterExtension extends ClassWriter {
	public ClassWriterExtension(int flags) {
		super(flags);
	}

	protected String getCommonSuperClass(String type1, String type2) {

		try {
			String replace1 = type1.replace('/', '.');
			String replace2 = type2.replace('/', '.');
			
			CtClass ctClass1 = ClassPool.getDefault().get(replace1);
			CtClass ctClass2 = ClassPool.getDefault().get(replace2);
			
			ContinuationMethodAnalyzer.isAssignable(replace1, replace2);
			
			ClassLoader classLoader = getClassLoader();
			if (ContinuationMethodAnalyzer.isAssignable(replace1, replace2)) {
				return type1;
			}
			if (ContinuationMethodAnalyzer.isAssignable(replace2, replace1)) {
				return type2;
			}
			if (ctClass1.isInterface() || ctClass2.isInterface()) {
				return "java/lang/Object";
			} else {
				CtClass class1;
				do {
					ctClass1 = ctClass1.getSuperclass();
				} while (!ContinuationMethodAnalyzer.isAssignable(ctClass1.getName(), replace2));
				return ctClass1.getName().replace('.', '/');
			}
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}