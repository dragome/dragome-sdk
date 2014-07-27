package com.dragome.compiler.ast;

import java.util.List;

import org.apache.bcel.generic.Type;

import com.dragome.compiler.Project;
import com.dragome.compiler.generators.AbstractVisitor;

public class MethodInvocation extends Expression
{

	private Expression expression;

	public MethodDeclaration methodDecl;

	public boolean isSpecial= false;

	private MethodBinding methodBinding;

	public MethodInvocation()
	{
	}

	public MethodInvocation(MethodDeclaration theMethodDecl)
	{
		methodDecl= theMethodDecl;
	}

	public MethodInvocation(MethodDeclaration theMethodDecl, MethodBinding theMethodBinding)
	{
		methodDecl= theMethodDecl;
		setMethodBinding(theMethodBinding);
	}

	public Type getTypeBinding()
	{
		if (methodBinding == null)
			return super.getTypeBinding();
		return methodBinding.getReturnType();
	}

	public boolean isSuper(String currentClassName)
	{
		if (!isSpecial)
			return false;

		if (methodBinding.isConstructor())
		{
			if (!(getExpression() instanceof ThisExpression))
				return false;
		}

		String name= methodBinding.getDeclaringClass().getClassName();
		if (currentClassName.equals(name))
			return false;

		return true;
	}

	public List getArguments()
	{
		ASTNodeStack stack= new ASTNodeStack();
		ASTNode node= getFirstChild();
		if (expression != null)
		{
			node= node.getNextSibling();
		}

		while (node != null)
		{
			stack.add(node);
			node= node.getNextSibling();
		}

		return stack;

	}

	public void addArgument(Expression argument)
	{
		widen(argument);
		appendChild(argument);
	}

	public Expression getExpression()
	{
		return expression;
	}

	public void setExpression(Expression targetExpression)
	{
		if (expression != null)
		{
			throw new RuntimeException("Expression is already set");
		}
		expression= targetExpression;
		widen(expression);
		insertBefore(expression, getFirstChild());
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public MethodBinding getMethodBinding()
	{
		return methodBinding;
	}

	public void setMethodBinding(MethodBinding theMethodBinding)
	{
		methodBinding= theMethodBinding;
		Project.getSingleton().addReference(methodDecl, this);
	}
}
