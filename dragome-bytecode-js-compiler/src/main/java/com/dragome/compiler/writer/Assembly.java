/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * This file is part of Dragome SDK.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

// Copyright 2011 The j2js Authors. All Rights Reserved.
//
// This file is part of j2js.
//
// j2js is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// j2js is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with j2js. If not, see <http://www.gnu.org/licenses/>.

package com.dragome.compiler.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.python.core.PyObject;
import org.python.google.common.collect.Lists;
import org.python.util.PythonInterpreter;

import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;
import com.dragome.compiler.DragomeJsCompiler;
import com.dragome.compiler.Project;
import com.dragome.compiler.generators.DragomeJavaScriptGenerator;
import com.dragome.compiler.generators.JavaScriptCompressor;
import com.dragome.compiler.type.Signature;
import com.dragome.compiler.type.TypeResolver;
import com.dragome.compiler.units.ClassUnit;
import com.dragome.compiler.units.ConstructorUnit;
import com.dragome.compiler.units.MemberUnit;
import com.dragome.compiler.units.ProcedureUnit;
import com.dragome.compiler.utils.Log;

public class Assembly {
	public List<String> entryPoints = new ArrayList<String>();
	private transient Log logger;
	private transient String entryPointClassName;
	private Project project;
	private Set<Signature> taintedSignatures = new LinkedHashSet<Signature>();
	private Set<Signature> unprocessedTaintedSignatures = new LinkedHashSet<Signature>();
	String[] patterns;
	private Collection<ClassUnit> resolvedTypes = new ArrayList<ClassUnit>();
	private transient File targetLocation;
	private ClasspathFileFilter classpathFilter;

	public Assembly() {
		// patterns= Utils.getProperty("dragomeJs.taintIfInstantiated").split(";");
		// for (int i= 0; i < patterns.length; i++)
		// {
		//
		// String pattern= patterns[i].replaceAll("\\s", "");
		// pattern= pattern.replaceAll("\\.", "\\\\.");
		// pattern= pattern.replaceAll("\\*", ".*");
		// pattern= pattern.replaceAll("\\(", "\\\\(");
		// pattern= pattern.replaceAll("\\)", "\\\\)");
		// patterns[i]= pattern;
		// }
	}

	private void pipeFileToStream(Writer writer, String relativeFilePath) throws IOException {
		ClasspathFile fileObject = DragomeJsCompiler.compiler.fileManager.getFileForInput(relativeFilePath);
		String content;
		if (DragomeJsCompiler.compiler.isCompression()) {
			JavaScriptCompressor compressor = new JavaScriptCompressor();
			content = compressor.compress(fileObject.openInputStream());
		} else {
			content = IOUtils.toString(fileObject.openInputStream());
		}
		writer.write(content);

		fileObject.close();
	}

	private void removeOldAssemblies(File assembly) {
		final String numericPostfixPattern = "-[0-9]*$";
		final String prefix = assembly.getName().replaceAll(numericPostfixPattern, "");

		File[] oldAssemblies = assembly.getParentFile().listFiles(new FilenameFilter() {
			public boolean accept(File dir1, String name) {
				return name.matches(prefix + numericPostfixPattern);
			}
		});

		if (oldAssemblies == null) {
			return;
		}

		for (File oldAssemblyDir : oldAssemblies) {
			for (File file : oldAssemblyDir.listFiles()) {
				file.delete();
			}
			oldAssemblyDir.delete();
		}
	}

	public int createAssembly() throws IOException {
		logger = Log.getLogger();
		logger.debug("Packing ...");

		removeOldAssemblies(targetLocation);

		String loaderName = DragomeJsCompiler.compiler.getTargetPlatform().toLowerCase();
		Writer writer;

		if ("javascript".equals(loaderName)) {
			writer = new FileWriter(targetLocation);
			pipeFileToStream(writer, "dragome/javascript/loaders/" + loaderName + ".js");
		} else {
			targetLocation.mkdirs();
			writer = new JunkWriter(targetLocation);
		}
		// writer.write("// Assembly generated by dragomeJs " + Utils.getVersion() + "
		// on " + Utils.currentTimeStamp() + "\n");

		writer.write("//***********************************************************************\n");
		writer.write("//* Generated with Dragome SDK Copyright (c) 2011-2016 Fernando Petrola *\n");
		writer.write("//***********************************************************************\n");
		writer.write("\n");

		// pipeFileToStream(writer, "javascript/q-3.0.js");

		pipeFileToStream(writer, "dragome/javascript/runtime.js");
		// writer.write("dragomeJs.assemblyVersion = 'dragomeJs Assembly " +
		// targetLocation.getName() + "@" + Utils.currentTimeStamp() + "';\n");

		writer.write("dragomeJs.userData = {};\n");

		// int classCount= 0;
		// for (ClassUnit fileUnit : project.getClasses())
		// {
		// if (!fileUnit.isTainted())
		// continue;
		// writer.write("dragomeJs.");
		// writer.write(DECLARECLASS);
		// writer.write("(\"" + fileUnit.getSignature() + "\"");
		// writer.write(", " + fileUnit.getSignature().getId());
		// writer.write(");\n");
		// classCount++;
		// }

		project.currentGeneratedMethods = 0;
//		try {
			long start = System.currentTimeMillis();

			Set<String> classesToRecompile = project.getClassesToRecompile();

//			List<List<String>> partition = Lists.partition(classesToRecompile, 4);

//			ExecutorService es = Executors.newCachedThreadPool();

			//extracted(classesToRecompile);
//			for (List<String> list : partition) {
//				es.execute(() -> extracted(list));
//			}

//			es.shutdown();
//			boolean finished = es.awaitTermination(4, TimeUnit.MINUTES);

			long end = System.currentTimeMillis();

			System.out.println("termino!: " + (end - start));

//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		if (DragomeJsCompiler.compiler.getSingleEntryPoint() != null) {
			Signature signature = project.getSignature(DragomeJsCompiler.compiler.getSingleEntryPoint());
			ClassUnit clazz = project.getClassUnit(signature.className());
			clazz.write(0, writer);
		} else {
			ClassUnit object = project.getJavaLangObject();
			object.write(0, writer);

			do {
				ClassUnit.oneWritten = false;
				for (ClassUnit cu : project.getClasses()) {
					// if (cu.isInterface)
					{
						cu.write(0, writer);
					}
				}
			} while (ClassUnit.oneWritten);

		}

		ClassUnit stringClazz = project.getClassUnit(String.class.getName());
		ClassUnit stringSuperClazz = stringClazz.getSuperUnit();

		Collection<MemberUnit> declaredMembers = new ArrayList<MemberUnit>(stringClazz.getDeclaredMembers());
		declaredMembers.addAll(stringSuperClazz.getDeclaredMembers());

		for (MemberUnit memberUnit : declaredMembers) {
			Signature signature = memberUnit.getSignature();
			String normalizeExpression = DragomeJavaScriptGenerator.normalizeExpression(signature);
			writer.write("String.prototype." + normalizeExpression + "= java_lang_String.prototype."
					+ normalizeExpression + ";\n");
		}

		writer.write("String.prototype.classname= \"java_lang_String\";\n");

		for (MemberUnit member : ClassUnit.stringInits) {
			String memberData = member.getData();
			member.setData(member.getData().substring(1));
			member.write(1, writer);
			member.setData(memberData);
			if (member instanceof ProcedureUnit) {
				project.currentGeneratedMethods++;
				writer.flush();
			}
		}

		// project.writeClinits(writer);

		if (getProject().getClassUnit("java.lang.String").isTainted()) {
			writer.write("String.prototype.clazz = java_lang_String;\n");
		}

		// writer.write("dragomeJs.onLoad('" + entryPointClassName +
		// "#main(java.lang.String[])void');\n");

		writer.write("javascript_Utils.$init$void();\n");

		project.writeSignatures(writer);

		writer.write("java_lang_Object.prototype.toString= function (){return this.$toString$java_lang_String();};\n"); // TODO
																														// mover
																														// despues
																														// de
																														// creacion
																														// de
																														// Object

		// writer.write("Array.prototype.$clone$java_lang_Object=
		// java_lang_Object.prototype.$clone$java_lang_Object;\n"); //TODO mover despues
		// de creacion de Object

		writeAnnotationsInsertion(writer);

		// writer.write("new " + mainClass + "();\n");
		// writer.write(mainClass + "." + mainMethod + "();\n");

		writer.write("onReady(function(){setupCheckCast(); _ed.executeMainClass();});");

		writer.close();

		return project.currentGeneratedMethods;
	}

	private void extracted(Set<String> classesToRecompile) {

		PythonInterpreter interpreter = new PythonInterpreter(null);
		InputStream is = TypeResolver.class.getClassLoader().getResourceAsStream("Lib/Krakatau/decompile.py");
		interpreter.execfile(is);
		Classpath classpath = project.getClasspath();
		String name = classpath.getEntries().get(0).getName();

		String collect = classesToRecompile.stream().map(s -> "'" + s + "'")
				.collect(Collectors.joining(","));
		String toEval = "decompileClass(['" + name + "'], " + "'', " + "[" + collect + "], " + "'temp2', " + "'true')";

		System.out.println(toEval);
		PyObject eval = interpreter.eval(toEval);
		System.out.println(eval);
	}

	private void writeAnnotationsInsertion(Writer writer) throws IOException {
		Set<String> typeDeclarations = getProject().getTypeDeclarationsWithAnnotations();

		for (String typeDeclaration : typeDeclarations) {
			String[] key = typeDeclaration.split("#");
			if (!"::::bytes".equals(key[2])) // TODO fixme: scala annotations
				writer.write(String.format("dragomeJs.addTypeAnnotation(\"%s\", \"%s\", \"%s\", \"%s\");\n", key[0],
						key[1], key[2], key[3].trim()));
		}
	}

	public void processTainted() {
		while (unprocessedTaintedSignatures.size() > 0) {
			processSingle(popSignature());
			if (unprocessedTaintedSignatures.size() == 0) {
				processOverWrittenMembers();
			}
		}
	}

	public void processSingle(Signature signature) {
		ClassUnit clazz = resolve(signature.className());
		File file = new File(signature.className().replace(".", "/"));
		if (classpathFilter.accept(clazz.getClassFile())) {
			String methodPart = signature.relativeSignature();
			boolean found = false;
			for (MemberUnit member : clazz.getMembers(methodPart)) {
				taint(member);
				found = true;
			}

			if (!found) {
				Log.getLogger().debug("No such method: " + signature);
				// throw new RuntimeException("No such method: " + signature);
			}
		}
	}

	private ClassUnit resolve(String className) {
		ClassUnit clazz = project.getClassUnit(className);

		if (className.startsWith("[")) {
			project.resolve(clazz);
		} else {
			project.resolve(clazz);
			taint(className + "#<clinit>()void");
		}

		resolvedTypes.add(clazz);

		return clazz;
	}

	public ClassUnit resolveNoTainting(String className, ClasspathFile classpathFile) {
		ClassUnit clazz = project.getClassUnit(className);

		if (className.startsWith("[")) {
			project.resolve(clazz);
		} else {
			project.resolve(clazz);
		}

		resolvedTypes.add(clazz);

		return clazz;
	}

	private void taintImplicitelyAccessedMembers(ClassUnit clazz) {

		for (MemberUnit member : clazz.getDeclaredMembers()) {
			// for (int i= 0; i < patterns.length; i++)
			{
				// if (member.getAbsoluteSignature().toString().matches(patterns[i]))
				{
					taint(member.getAbsoluteSignature());
				}
			}
		}
	}

	private void taintIfSuperTainted(ClassUnit clazz) {
		if (clazz.getName().equals("java.lang.Object"))
			return;

		for (MemberUnit member : clazz.getDeclaredMembers()) {
			for (ClassUnit superType : clazz.getSupertypes()) {
				Signature signature = Project.getSingleton().getSignature(superType.getName(),
						member.getSignature().toString());
				// if (taintedSignatures.contains(signature))
				{
					taint(member);
				}
			}
		}
	}

	private void taintTargetSignatures(ProcedureUnit method) {
		for (Signature targetSignature : method.getTargetSignatures()) {
			taint(targetSignature);
		}
	}

	private void processOverWrittenMembers() {
		Iterator<ClassUnit> classIterator = resolvedTypes.iterator();
		while (classIterator.hasNext()) {
			ClassUnit clazz = classIterator.next();
			if (clazz.isConstructorTainted)
				taintIfSuperTainted(clazz);
			;
		}
	}

	public void taint(String signature) {
		signature = signature.replaceAll("\\s", "");

		Signature s = Project.getSingleton().getSignature(signature);
		if (s.isClass()) {
			ClassUnit clazz = resolve(s.className());

			for (MemberUnit member : clazz.getDeclaredMembers()) {
				taint(member.getAbsoluteSignature());
			}
		} else {
			taint(s);
		}

	}

	private Signature popSignature() {
		Iterator<Signature> iter = unprocessedTaintedSignatures.iterator();
		Signature signature = iter.next();
		iter.remove();
		return signature;
	}

	public void taint(Signature signature) {
		if (!signature.toString().contains("#")) {
			throw new IllegalArgumentException("Signature must be field or method: " + signature);
		}

		if (taintedSignatures.contains(signature))
			return;

		taintedSignatures.add(signature);
		unprocessedTaintedSignatures.add(signature);
	}

	public void taint(MemberUnit member) {
		member.setTainted();

		member.getDeclaringClass().setSuperTainted();
		if (member instanceof ProcedureUnit) {
			taintTargetSignatures((ProcedureUnit) member);
			if (member instanceof ConstructorUnit) {
				member.getDeclaringClass().isConstructorTainted = true;
				taintImplicitelyAccessedMembers(member.getDeclaringClass());
			}
		}
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

	public String getEntryPointClassName() {
		return entryPointClassName;
	}

	public Assembly setEntryPointClassName(String entryPointClassName) {
		this.entryPointClassName = entryPointClassName;
		return this;
	}

	public File getTargetLocation() {
		return targetLocation;
	}

	public void setTargetLocation(File targetLocation) {
		this.targetLocation = targetLocation;
	}

	public void addEntryPoint(String memberSignature) {
		entryPoints.add(memberSignature);
	}

	public void setClasspathFilter(ClasspathFileFilter classpathFilter) {
		this.classpathFilter = classpathFilter;
	}

}
