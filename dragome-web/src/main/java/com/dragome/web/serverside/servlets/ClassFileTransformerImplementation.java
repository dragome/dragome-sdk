package com.dragome.web.serverside.servlets;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.services.ServiceLocator;

public class ClassFileTransformerImplementation implements ClassFileTransformer {
	BytecodeTransformer bytecodeTransformer = ServiceLocator.getInstance().getConfigurator()
			.getBytecodeTransformer();

	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		byte[] result = classfileBuffer;
		String replace = className.replace("/", ".");
		System.clearProperty("invokeDynamicBackporter");
		if (bytecodeTransformer.requiresTransformation(replace)) {
			result = bytecodeTransformer.transform(replace, classfileBuffer);
		}
		return result;
	}
}