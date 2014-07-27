package com.dragome.compiler.ast;

import com.dragome.compiler.generators.AbstractVisitor;

public class PStarExpression extends Expression
{

	static public Operator INCREMENT= new Operator("++");

	static public Operator DECREMENT= new Operator("--");

	static public class Operator
	{

		private String token;

		Operator(String theToken)
		{
			token= theToken;
		}

		public String toString()
		{
			return token;
		}

		public Operator complement()
		{
			if (this == PStarExpression.INCREMENT)
				return PStarExpression.DECREMENT;
			else if (this == PStarExpression.DECREMENT)
				return PStarExpression.INCREMENT;
			else
				throw new RuntimeException("Operation not supported for " + this);
		}

	}

	private ASTNode operand;

	private Operator operator;

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public ASTNode getOperand()
	{
		return operand;
	}

	public void setOperand(ASTNode theOperand)
	{
		widen(theOperand);
		operand= theOperand;
	}

	public Operator getOperator()
	{
		return operator;
	}

	public void setOperator(Operator theOperator)
	{
		operator= theOperator;
	}
}
