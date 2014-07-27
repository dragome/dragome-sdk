package com.dragome.compiler.ast;

public class OperandState
{

	int code;

	int beginIndex;

	int endIndex;

	ASTNode stmt;

	OperandState(int theCode)
	{
		code= theCode;
	}

	OperandState(int theCode, int theBeginIndex, ASTNode theStmt)
	{
		code= theCode;
		beginIndex= theBeginIndex;
		stmt= theStmt;
	}

	public String toString()
	{
		return "State: " + code + " " + stmt;
	}
}
