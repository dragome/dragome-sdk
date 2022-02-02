package com.dragome.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class AgentHelper {
	private static final Logger LOGGER = Logger.getLogger(AgentHelper.class.getSimpleName());

	/**
	 * An agent must be specified as a .jar where the manifest has an Agent-Class attribute. Additionally, in order
	 * to be able to redefine classes, the Can-Redefine-Classes attribute must be true.
	 *
	 * This method creates such an agent Jar as a temporary file. The Agent-Class is this class. If the returned Jar
	 * is loaded as an agent then {@link RedefineClassAgent#agentmain(String, Instrumentation)} will be called by the JVM.
	 *
	 * @return a temporary {@link File} that points at Jar that packages this class.
	 * @throws IOException if agent Jar creation failed.
	 */
	static File createAgentJarFile() throws IOException {
		File jarFile = File.createTempFile("agent", ".jar");
		jarFile.deleteOnExit();
	
		// construct a manifest that allows class redefinition
		Manifest manifest = new Manifest();
		Attributes mainAttributes = manifest.getMainAttributes();
		mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		mainAttributes.put(new Attributes.Name("Agent-Class"), RedefineClassAgent.class.getName());
		mainAttributes.put(new Attributes.Name("Can-Retransform-Classes"), "true");
		mainAttributes.put(new Attributes.Name("Can-Redefine-Classes"), "true");
	
		try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest)) {
			// add the agent .class into the .jar
			JarEntry agent = new JarEntry(RedefineClassAgent.class.getName().replace('.', '/') + ".class");
			jos.putNextEntry(agent);
	
			// dump the class bytecode into the entry
			ClassPool pool = ClassPool.getDefault();
			CtClass ctClass = pool.get(RedefineClassAgent.class.getName());
			jos.write(ctClass.toBytecode());
			jos.closeEntry();
		} catch (CannotCompileException | NotFoundException e) {
			// Realistically this should never happen.
			AgentHelper.LOGGER.log(Level.SEVERE, "Exception while creating RedefineClassAgent jar.", e);
			throw new IOException(e);
		}
	
		return jarFile;
	}

}
