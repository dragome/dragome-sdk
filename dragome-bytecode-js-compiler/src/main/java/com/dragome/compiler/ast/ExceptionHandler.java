package com.dragome.compiler.ast;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.generic.Type;

public class ExceptionHandler extends ASTNode
{

	private CodeException codeException;

	public ExceptionHandler(CodeException theCodeException)
	{
		codeException= theCodeException;
	}

	public int getStartPC()
	{
		return codeException.getStartPC();
	}

	public int getEndPC()
	{
		return codeException.getEndPC();
	}

	public int getHandlerPC()
	{
		return codeException.getHandlerPC();
	}

	public Type getCatchType(ConstantPool cp)
	{
		if (codeException.getCatchType() == 0)
			return null;
		String signature= cp.getConstantString(codeException.getCatchType(), Constants.CONSTANT_Class);
		return Type.getType("L" + signature + ";");
	}

	public boolean isDefault()
	{
		return codeException.getCatchType() == 0;
	}

}
