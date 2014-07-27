package com.dragome.compiler.ast;

import org.apache.bcel.generic.ObjectType;

import com.dragome.compiler.Project;
import com.dragome.compiler.generators.AbstractVisitor;

public abstract class FieldAccess extends Expression
{

	private String name;

	private ObjectType type;

	public FieldAccess()
	{
	}

	public void initialize(MethodDeclaration methodDecl)
	{
		Project.getSingleton().addReference(methodDecl, this);
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public boolean isSame(Object obj)
	{
		if (!(obj instanceof FieldAccess))
			return false;
		FieldAccess other= (FieldAccess) obj;
		if (!name.equals(other.name))
			return false;
		if (getExpression() instanceof VariableBinding && other.getExpression() instanceof VariableBinding)
		{
			VariableBinding vba= (VariableBinding) getExpression();
			VariableBinding vbb= (VariableBinding) other.getExpression();
			return vba.getVariableDeclaration() == vbb.getVariableDeclaration();
		}
		return false;
	}

	public Expression getExpression()
	{
		return (Expression) getFirstChild();
	}

	public void setExpression(Expression expression)
	{
		widen(expression);
		removeChildren();
		appendChild(expression);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String theName)
	{
		name= theName;
	}

	public String toString()
	{
		return super.toString() + " " + name;
	}

	public ObjectType getType()
	{
		return type;
	}

	public void setType(ObjectType theType)
	{
		if (type != null)
			throw new RuntimeException("Type is already set");
		type= theType;
	}

}
