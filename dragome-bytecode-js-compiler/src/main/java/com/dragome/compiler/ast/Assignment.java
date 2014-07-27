package com.dragome.compiler.ast;

import java.util.LinkedHashMap;

import com.dragome.compiler.generators.AbstractVisitor;

public class Assignment extends Expression
{

	static public class Operator
	{

		static private LinkedHashMap<String, Operator> opsByToken= new LinkedHashMap<String, Operator>();

		static public Operator lookup(String token)
		{
			return opsByToken.get(token);
		}

		static public Operator ASSIGN= new Operator("=");

		static public Operator PLUS_ASSIGN= new Operator("+=");

		static public Operator MINUS_ASSIGN= new Operator("-=");

		static public Operator TIMES_ASSIGN= new Operator("*=");

		static public Operator DIVIDE_ASSIGN= new Operator("/=");

		static public Operator BIT_AND_ASSIGN= new Operator("&=");

		static public Operator BIT_OR_ASSIGN= new Operator("|=");

		static public Operator BIT_XOR_ASSIGN= new Operator("^=");

		static public Operator REMAINDER_ASSIGN= new Operator("%=");

		static public Operator LEFT_SHIFT_ASSIGN= new Operator("<<=");

		static public Operator RIGHT_SHIFT_SIGNED_ASSIGN= new Operator(">>=");

		static public Operator RIGHT_SHIFT_UNSIGNED_ASSIGN= new Operator(">>>=");

		private String token;

		Operator(String theToken)
		{
			token= theToken;
			opsByToken.put(theToken, this);
		}

		public String toString()
		{
			return token;
		}
	}

	private Operator operator;

	public Assignment(Operator theOperator)
	{
		super();
		operator= theOperator;
	}

	public void visit(AbstractVisitor visitor)
	{
		visitor.visit(this);
	}

	public void setRightHandSide(Expression rightHandSide)
	{
		widen(rightHandSide);
		setChildAt(1, rightHandSide);
	}

	public Expression getRightHandSide()
	{
		return (Expression) getChildAt(1);
	}

	public void setLeftHandSide(Expression leftHandSide)
	{
		setChildAt(0, leftHandSide);
	}

	public Expression getLeftHandSide()
	{
		return (Expression) getChildAt(0);
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
