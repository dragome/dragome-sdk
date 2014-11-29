package com.dragome.compiler.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.bcel.generic.ObjectType;

import com.dragome.compiler.Project;
import com.dragome.compiler.generators.AbstractVisitor;
import com.dragome.compiler.parser.Parser;

public class TypeDeclaration extends ASTNode
{

	private ObjectType type;

	private ObjectType superType;

	private ArrayList<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

	private List<VariableDeclaration> fields = new ArrayList<VariableDeclaration>();

	private int accessFlags;

	private Map<String, String> annotations;

	public Map<String, String> getAnnotations()
	{
		return annotations;
	}

	public void setAnnotations(Map<String, String> annotations)
	{
		this.annotations = annotations;
	}

	public TypeDeclaration(ObjectType theType, int theAccessFlags, Map<String, String> annotations)
	{
		type = theType;
		accessFlags = theAccessFlags;
		this.annotations = annotations;
	}

	public void visit(AbstractVisitor visitor)
	{
		if (isAllowedToCompile())
			visitor.visit(this);
	}

	public MethodDeclaration[] getMethods()
	{
		MethodDeclaration[] a = new MethodDeclaration[methods.size()];
		return methods.toArray(a);
	}

	public int getAccess()
	{
		return accessFlags;
	}

	public void addMethod(MethodDeclaration method)
	{
		method.setParentNode(this);
		methods.add(method);
	}

	public ObjectType getType()
	{
		return type;
	}

	public String getPackageName()
	{
		String name = type.getClassName();
		int index = name.lastIndexOf('.');
		if (index != -1)
			return name.substring(0, index);
		else
			return name;
	}

	public String getClassName()
	{
		return type.getClassName();
	}

	public String getUnQualifiedName()
	{
		String name = type.getClassName();
		int index = name.lastIndexOf('.');
		if (index != -1)
			return name.substring(index + 1);
		else
			return name;
	}

	public List<VariableDeclaration> getFields()
	{
		return fields;
	}

	public void addField(VariableDeclaration field)
	{
		fields.add(field);
		Project.getSingleton().getOrCreateFieldUnit(type, field.getName());
	}

	public ObjectType getSuperType()
	{
		return superType;
	}

	public void setSuperType(ObjectType newSuperType)
	{
		superType = newSuperType;
	}

	public String toString()
	{
		return type.getClassName();
	}

	public boolean isAllowedToCompile() {
		return !annotations.containsKey(Parser.DONTPARSE);
	}

}
