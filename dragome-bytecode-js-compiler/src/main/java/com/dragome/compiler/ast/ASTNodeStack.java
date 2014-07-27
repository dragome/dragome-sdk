package com.dragome.compiler.ast;

import java.util.ArrayList;

public class ASTNodeStack extends ArrayList<ASTNode>
{

	public ASTNodeStack()
	{
		super();
	}

	public ASTNodeStack(ASTNodeStack other)
	{
		super(other);
	}

	public ASTNodeStack(Expression expression)
	{
		super();
		push(expression);
	}

	public Expression pop()
	{
		if (size() == 0)
			throw new RuntimeException("Cannot pop empty stack");
		return (Expression) remove(size() - 1);
	}

	public void push(ASTNode node)
	{
		add(node);
	}

	public void rotate(int offset)
	{
		if (offset == 0)
			return;
		if (offset > size())
			throw new IndexOutOfBoundsException();
		ASTNode node= pop();
		add(size() - offset, node);
	}

	public Expression peek()
	{
		return peek(0);
	}

	private static Object safeCast(Object object, Class clazz)
	{
		if (!clazz.isInstance(object))
		{
			throw new RuntimeException("Expected " + clazz + ", but was " + object);
		}
		return object;
	}

	public Expression peek(int offset)
	{
		if (size() - 1 < offset)
			return null;
		return (Expression) ASTNodeStack.safeCast(get(size() - 1 - offset), Expression.class);
	}
}
