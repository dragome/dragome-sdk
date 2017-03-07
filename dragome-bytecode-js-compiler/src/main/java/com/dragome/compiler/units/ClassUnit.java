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

package com.dragome.compiler.units;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.output.StringBuilderWriter;

import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.compiler.DragomeJsCompiler;
import com.dragome.compiler.Project;
import com.dragome.compiler.generators.DragomeJavaScriptGenerator;
import com.dragome.compiler.type.Signature;
import com.dragome.compiler.type.TypeCollector;
import com.dragome.compiler.utils.Log;

public class ClassUnit extends Unit
{
	public static final String STATIC_MEMBER= "#static-member#";

	static final long serialVersionUID= 1;

	private long lastCompiled;

	public long getLastModified()
	{
		return getClassFile().getLastModified();
	}

	public long getCRC()
	{
		return getClassFile().getCRC();
	}

	private Map<String, MemberUnit> declaredMembers;

	private ClassUnit superUnit;

	private Collection<ClassUnit> interfaces;

	private Collection<ClassUnit> subUnits;

	public boolean isInterface= false;

	public boolean isConstructorTainted= false;

	public Map<String, String>[] annotations;

	private Project project;

	private transient boolean isResolved= false;

	private transient ClasspathFile classFile;

	private List<ClassUnit> implementors= new ArrayList<ClassUnit>();

	private transient boolean written= false;

	private String generatedJs;

	private long lastCRC;

	public static boolean oneWritten= false;

	public List<ClassUnit> getImplementors()
	{
		return implementors;
	}

	public void setImplementors(List<ClassUnit> implementors)
	{
		this.implementors= implementors;
	}

	public static List<MemberUnit> stringInits= new ArrayList<MemberUnit>();

	public ClassUnit()
	{
	}

	public ClassUnit(Project theProject, Signature theSignature)
	{
		project= theProject;

		interfaces= new LinkedHashSet<ClassUnit>();
		declaredMembers= new LinkedHashMap<String, MemberUnit>();
		subUnits= new LinkedHashSet<ClassUnit>();
		lastCompiled= -1;
		setSignature(theSignature);
	}

	public void clear()
	{
		lastCompiled= -1;
		removeInterfaces();
		setSuperUnit(null);
		declaredMembers.clear();
		generatedJs= null;
	}

	public boolean isUpToDate()
	{
		return lastCRC == getCRC();
	}

	public Collection<ClassUnit> getInterfaces()
	{
		return interfaces;
	}

	public void addInterface(ClassUnit interfaceUnit)
	{
		interfaces.add(interfaceUnit);
		interfaceUnit.addImplementor(this);
	}

	private void addImplementor(ClassUnit classUnit)
	{
		implementors.add(classUnit);
	}

	private void removeInterfaces()
	{
		Iterator iter= interfaces.iterator();
		while (iter.hasNext())
		{
			ClassUnit interfaceUnit= (ClassUnit) iter.next();
			interfaceUnit.removeSubUnit(this);
			iter.remove();
		}
	}

	public void addSubUnit(ClassUnit subUnit)
	{
		if (subUnit == null)
			throw new NullPointerException();
		subUnits.add(subUnit);
	}

	public void removeSubUnit(ClassUnit subUnit)
	{
		if (subUnit == null)
			throw new NullPointerException();
		subUnits.remove(subUnit);
	}

	public Collection<ClassUnit> getSubUnits()
	{
		return subUnits;
	}

	public MemberUnit getDeclaredMember(String signature)
	{
		if (signature == null)
			throw new NullPointerException();
		return declaredMembers.get(signature);
	}

	public Collection<ClassUnit> getSupertypes()
	{
		TypeCollector collector= new TypeCollector();
		project.visitSuperTypes(this, collector);
		return collector.collectedTypes;
	}

	public Collection<MemberUnit> getMembers(String signature)
	{
		if (signature == null)
			throw new NullPointerException();
		ArrayList<MemberUnit> list= new ArrayList<MemberUnit>();

		for (ClassUnit clazz : getSupertypes())
		{
			MemberUnit member= clazz.getDeclaredMember(signature);
			if (member != null)
				list.add(member);
		}

		return list;
	}

	public Collection<MemberUnit> getDeclaredMembers()
	{
		if (!isResolved)
			throw new RuntimeException("Class is not yet resolved: " + getName());
		return declaredMembers.values();
	}

	public void addMemberUnit(MemberUnit unit)
	{
		declaredMembers.put(unit.getSignature().toString(), unit);
	}

	public ClassUnit getSuperUnit()
	{
		return superUnit;
	}

	public void setSuperUnit(ClassUnit theSuperUnit)
	{
		if (superUnit != null)
		{
			superUnit.removeSubUnit(this);
		}
		superUnit= theSuperUnit;
		if (superUnit != null)
		{
			superUnit.addSubUnit(this);
		}
	}

	public void write(int depth, Writer writer2, String... annotationDefaultFound) throws IOException
	{
		if (!isTainted() || !isResolved() || isWritten())
			return;

		Writer writer= new StringBuilderWriter();

		oneWritten= true;
		written= true;

		for (ClassUnit child : getSupertypes())
		{
			child.setTainted();
			child.write(depth, writer2);
		}

		if (getSuperUnit() != null)
		{
			getSuperUnit().setTainted();
			getSuperUnit().write(depth, writer2);
		}

		for (ClassUnit child : getInterfaces())
		{
			child.setTainted();
			child.write(depth, writer2);
		}

		if (generatedJs == null)
			generatedJs= generateJsCode(depth, writer);

		writer2.write(generatedJs);

		taintRelated(depth, writer2);
	}

	private String generateJsCode(int depth, Writer writer) throws IOException
	{
		Log.getLogger().debug(getIndent(depth) + this);

		if (getData() != null)
		{
			writer.write(getData());
		}
		else
		{
			throw new RuntimeException("Cannot compile the class: " + getName());
		}

		Set<MemberUnit> notImplementedMethods= getNotImplementedMethods();

		if (interfaces.size() > 0)
		{
			String extendOperator= "implement";
			if (isInterface)
				extendOperator= "extend";
			//	    writer.write("_T.interfaces = [");
			writer.write(extendOperator + ": [");
			int i= 0;
			for (ClassUnit interFace : interfaces)
			{
				if (i++ > 0)
					writer.write(", ");
				writer.write(DragomeJavaScriptGenerator.normalizeExpression(interFace.getSignature().toString()));
			}
			writer.write("],\n");
		}

		writer.write("members:\n");
		writer.write("{\n");

		MemberUnit clinitMethod= null;
		List<MemberUnit> staticMethods= new ArrayList<MemberUnit>();

		boolean first= true;

		for (MemberUnit member : getDeclaredMembers())
		{
			if (member.getData() != null && member.getData().startsWith(STATIC_MEMBER))
			{
				staticMethods.add(member);
			}
			else if (member.getData() != null && member.getData().contains("_dragomeJs.StringInit"))
			{
				stringInits.add(member);
			}
			else
			{
				if (member.getData() != null && member.getData().trim().length() > 0)
				{
					if (!first)
						writer.write(",\n");
					first= false;

					writeMethodAlternative(depth, writer, member);

					if (member instanceof ProcedureUnit)
					{
						project.currentGeneratedMethods++;
						writer.flush();
					}
				}
			}
			if (isClinit(member))
				clinitMethod= member;
		}

		for (MemberUnit member : notImplementedMethods)
		{
			if (!first)
				writer.write(",\n");

			first= false;

			String methodData= member.getData().replace(STATIC_MEMBER, "");
			methodData= methodData.substring(0, methodData.indexOf("{"));
			writer.write(methodData);
			writer.write("{\n return ");
			writer.write(member.getDeclaringClass().toString().replace(".", "_"));
			writer.write(".$$members.");
			writer.write(methodData.replace(": function ()", ".call (this)").replace(": function (", ".call (this, "));
			writer.write("}\n");

			if (member instanceof ProcedureUnit)
			{
				project.currentGeneratedMethods++;
				writer.flush();
			}
		}

		writer.write("\n},\n");
		writer.write("statics:\n");
		writer.write("{\n");

		writeClinit(depth, writer, clinitMethod, staticMethods);

		boolean hasStaticMembers= staticMethods.size() > 0;
		if (hasStaticMembers)
			writer.write(",");

		writer.write("\n");

		first= true;

		for (MemberUnit member : staticMethods)
		{
			if (member != clinitMethod)
			{
				if (!first)
					writer.write(",\n");

				first= false;

				String memberData= member.getData();
				member.setData(member.getData().replace(STATIC_MEMBER, ""));

				writeMethodAlternative(depth, writer, member);

				member.setData(memberData);
				if (member instanceof ProcedureUnit || member instanceof FieldUnit)
				{
					if (member instanceof ProcedureUnit)
						project.currentGeneratedMethods++;

					writer.flush();
				}
			}
		}

		first= addSuperStaticMethods(writer, !hasStaticMembers ^ first, staticMethods);

		//		addAnnotationsAsStaticMember(writer, first);

		writer.write("\n}\n");

		writer.write("\n}\n\n");
		writer.write(");\n");

		return writer.toString();
	}

	private void writeClinit(int depth, Writer writer, MemberUnit clinitMethod, List<MemberUnit> staticMethods) throws IOException
	{
		String superStaticFields= createSuperStaticFieldsReferences(depth, clinitMethod, staticMethods);

		if (clinitMethod != null)
		{
			String name= DragomeJavaScriptGenerator.normalizeExpression(clinitMethod.getDeclaringClass().getName());
			String replace= clinitMethod.getData().replace("this.", name + ".");
			replace= replace.replaceFirst("\\{", "{ this.\\$\\$clinit_=function(){return this};\n" + superStaticFields.replace("$", "\\$"));
			replace= replace.substring(0, replace.length() - 2) + "\n return this;\n}";

			String memberData= clinitMethod.getData();
			clinitMethod.setData(replace.replace(STATIC_MEMBER, ""));
			clinitMethod.write(depth, writer);
			clinitMethod.setData(memberData);
			String modifyMethodName= DragomeJavaScriptGenerator.normalizeExpression(clinitMethod.getSignature());
			project.getClinits().add("initClass(" + name + ");");
		}
		else
		{
			String replace= "$$clinit_: function(){this.$$clinit_=function(){return this};\n" + superStaticFields + " return this;}";
			writer.write(replace);
		}
	}

	private String createSuperStaticFieldsReferences(int depth, MemberUnit clinitMethod, List<MemberUnit> staticMethods) throws IOException
	{
		boolean first= true;
		StringBuilder result= new StringBuilder();

		if (superUnit != null)
			for (MemberUnit member : superUnit.getDeclaredMembers())
			{
				if (member.getData() != null && member.getData().startsWith(STATIC_MEMBER))
				{
					//					if (!containsSignature(member.getSignature(), staticMethods))
					if (!(member instanceof MethodUnit))
					{
						if (!member.toString().equals("java.lang.Object#hashCodeCount"))
						{
							if (!containsSignature(member.getSignature(), staticMethods))
							{
								String methodData= member.getData().replace(STATIC_MEMBER, "");
								String substring= methodData.substring(0, methodData.indexOf(":"));
								String fieldName= DragomeJavaScriptGenerator.normalizeExpression(getName()) + ".$$clinit_()." + substring + "=" + DragomeJavaScriptGenerator.normalizeExpression(member.getDeclaringClass().getName()) + ".$$clinit_()." + substring;

								result.append(fieldName);
								result.append(";\n");
								first= false;

								FieldUnit newField= new FieldUnit(member.getSignature(), this);
								newField.setData(member.getData());
								addMemberUnit(newField);
							}
						}
					}
				}
			}

		return result.toString();
	}

	private void addAnnotationsAsStaticMember(Writer writer, boolean first) throws IOException
	{
		if (!first)
			writer.write(",\n");

		writer.write("{\n");

		for (Entry<String, String> entry : annotationsValues.entrySet())
		{
			writer.write(entry.getKey() + "\n");
			writer.write(entry.getValue() + "\n");
		}

		writer.write("}\n");
	}

	private Set<MemberUnit> getNotImplementedMethods()
	{
		Set<MemberUnit> interfacesMembers= new HashSet<MemberUnit>();
		getDeclaredMembersInInterfaces(this, interfacesMembers);

		Set<MemberUnit> implementedMembers= new HashSet<MemberUnit>();
		getImplementedMembersInHierarchy(this, implementedMembers);

		interfacesMembers.removeAll(implementedMembers);
		if (isImplementing(InvocationHandler.class) || isAbstract || isInterface || interfacesMembers.size() <= 0 || interfacesMembers.isEmpty())
			interfacesMembers.clear();

		return interfacesMembers;
	}

	protected boolean isImplementing(Class<InvocationHandler> class1)
	{
		if (getSuperUnit() != null && getSuperUnit().isImplementing(class1))
			return true;

		for (ClassUnit interfaz : getInterfaces())
		{
			if (interfaz.toString().equals(class1.getName()) || interfaz.isImplementing(class1))
				return true;
		}
		return false;
	}

	private void getImplementedMembersInHierarchy(ClassUnit classUnit, Collection<MemberUnit> implementedMembers)
	{
		if (classUnit != null)
		{
			if (isInterface)
				return;

			implementedMembers.addAll(filterMethods(classUnit.getDeclaredMembers()));
			getImplementedMembersInHierarchy(classUnit.getSuperUnit(), implementedMembers);
		}
	}

	private Collection<MethodUnit> filterMethods(Collection<MemberUnit> declaredMembers2)
	{
		Collection<MethodUnit> result= new ArrayList<MethodUnit>();
		for (MemberUnit memberUnit : declaredMembers2)
		{
			if (memberUnit instanceof MethodUnit)
				result.add((MethodUnit) memberUnit);
		}
		return result;
	}

	private void getDeclaredMembersInInterfaces(ClassUnit classUnit, Collection<MemberUnit> interfacesMembers)
	{
		if (classUnit != null)
		{
			if (isInterface)
				interfacesMembers.addAll(filterMethods(classUnit.getDeclaredMembers()));

			for (ClassUnit interfaceUnit : classUnit.getInterfaces())
			{
				interfacesMembers.addAll(filterMethods(interfaceUnit.getDeclaredMembers()));
				interfaceUnit.getDeclaredMembersInInterfaces(interfaceUnit, interfacesMembers);
			}

			getDeclaredMembersInInterfaces(classUnit.getSuperUnit(), interfacesMembers);
		}
	}

	private boolean isClinit(MemberUnit member)
	{
		return member.getSignature().toString().equals("<clinit>()void");
	}

	private boolean addSuperStaticMethods(Writer writer, boolean first, List<MemberUnit> staticMethods) throws IOException
	{
		if (superUnit != null)
			for (MemberUnit member : superUnit.getDeclaredMembers())
			{
				if (member.getData() != null && member.getData().startsWith(STATIC_MEMBER))
				{
					if (member instanceof MethodUnit)
					{
						if (!isClinit(member))
							if (!containsSignature(member.getSignature(), staticMethods))
							{
								addMemberUnit(member);
								String methodData= member.getData().replace(STATIC_MEMBER, "");
								String substring= methodData.substring(0, methodData.indexOf("{"));
								String methodName= substring.replace(": function", "").replace("\n", "");
								substring+= "{ \n\t return this.superclass." + methodName + ";\n}";

								if (!first)
									writer.write(",\n");

								writer.write(substring);
								first= false;
							}
					}
				}
			}

		return first;
	}
	private boolean containsSignature(Signature signature, List<MemberUnit> staticMethods)
	{
		for (MemberUnit memberUnit : staticMethods)
		{
			if (memberUnit.getSignature().equals(signature))
				return true;
		}

		return false;
	}

	private void writeMethodAlternative(int depth, Writer writer, MemberUnit member) throws IOException
	{
		if (member instanceof MethodUnit)
		{
			MethodUnit methodUnit= (MethodUnit) member;
			String normalizedSignature= DragomeJavaScriptGenerator.normalizeExpression(methodUnit.getSignature());
		}
		String normalizedClassname= DragomeJavaScriptGenerator.normalizeExpression(member.getDeclaringClass().getName());
		Project.getSingleton().getWrittenSignatures().add(normalizedClassname/* + "|" + normalizedSignature*/);

		if (member instanceof ProcedureUnit && notReversibleMethods.contains(((ProcedureUnit) member).getNameAndSignature()))
		{
			ProcedureUnit methodUnit= (ProcedureUnit) member;
			writer.write(extractMethodDefinition(alternativeCompilation, methodUnit.getNameAndSignature()));
		}
		else
		{
			if (member instanceof MethodUnit)
			{
				String annotationDefaultFound= null;

				if (!annotationDefaults.isEmpty())
					for (Entry<String, String> annotationDefault : annotationDefaults.entrySet())
						if (member.getSignature() != null && member.getSignature().toString().startsWith(annotationDefault.getKey() + "("))
							annotationDefaultFound= annotationDefault.getValue();

				MethodUnit methodUnit= (MethodUnit) member;
				if (annotationDefaultFound != null)
				{
					methodUnit.write(depth + 1, writer, annotationDefaultFound);
					return;
				}
			}

			member.write(depth + 1, writer);
		}
	}

	private String extractMethodDefinition(String compiledCode, String nameAndSignature)
	{
		int startIndex= compiledCode.indexOf("start of " + nameAndSignature);
		int endIndex= compiledCode.indexOf("end of " + nameAndSignature);

		String part= compiledCode.substring(startIndex, endIndex);
		String result= part.substring(part.indexOf("*/"), part.lastIndexOf("}") + 1);
		result= result.substring(result.indexOf("$"));
		return result;
	}

	private void taintRelated(int depth, Writer writer) throws IOException
	{
		if (!"java.lang.Object".equals(toString()))
			for (String dependency : dependencies)
			{
				if (dependency.replace("[", "").length() == 1)
					continue;

				ClassUnit dependencyClassUnit= project.getClassUnit(dependency);
				dependencyClassUnit.setTainted();
				//		dependencyClassUnit.write(depth, writer);
			}

		for (ClassUnit child : getSubUnits())
		{
			child.setTainted();
			child.write(depth, writer);
		}

		for (ClassUnit child : getImplementors())
		{
			child.setTainted();
			//	    child.write(depth, writer);
		}
	}

	private boolean isWritten()
	{
		return written;
	}

	void setSignature(Signature theSignature)
	{
		super.setSignature(theSignature);
	}

	public ClasspathFile getClassFile()
	{
		if (classFile == null)
		{
			classFile= DragomeJsCompiler.compiler.fileManager.getFileForInput(getSignature().toString().replaceAll("\\.", "/") + ".class");
		}
		return classFile;
	}

	public void setClassFile(ClasspathFile classFile)
	{
		this.classFile= classFile;
	}

	public void setLastCompiled(long theLastCompiled)
	{
		lastCompiled= theLastCompiled;
	}

	public boolean isResolved()
	{
		return isResolved;
	}

	public void setSuperTainted()
	{
		ClassUnit clazz= this;
		do
		{
			clazz.setTainted();
			clazz= clazz.getSuperUnit();
		}
		while (clazz != null);

		for (ClassUnit i : interfaces)
		{
			i.setSuperTainted();
		}
	}

	public void setResolved(boolean theIsResolved)
	{
		isResolved= theIsResolved;
	}

	public String getName()
	{
		return getSignature().className();
	}

	public Project getProject()
	{
		return project;
	}

	private List<String> dependencies= new ArrayList<String>();

	private byte[] bytecode;

	public byte[] getBytecode()
	{
		return bytecode;
	}

	private List<String> notReversibleMethods= new ArrayList<String>();

	private String alternativeCompilation;

	public boolean isAbstract= false;

	private Map<String, String> annotationsValues;

	private Map<String, String> annotationDefaults= new HashMap<>();

	public List<String> getNotReversibleMethods()
	{
		return notReversibleMethods;
	}

	public void addDependency(String dependency)
	{
		dependencies.add(dependency);
	}

	public void setBytecodeArrayI(byte[] bytecode)
	{
		this.bytecode= bytecode;
	}

	public void addNotReversibleMethod(String methodNameSignature)
	{
		notReversibleMethods.add(methodNameSignature);
	}

	public void setAlternativeCompilation(String alternativeCompilation)
	{
		this.alternativeCompilation= alternativeCompilation;
	}

	public void setAnnotations(Map<String, String> annotationsValues)
	{
		this.annotationsValues= annotationsValues;
	}

	public void setLastCRC(long crc)
	{
		lastCRC= crc;
	}

	public void addAnnotationDefault(String name, String annotationDefault)
	{
		annotationDefaults.put(name, annotationDefault);
	}
}
