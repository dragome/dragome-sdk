package com.dragome.compiler.units;

import com.dragome.compiler.type.Signature;

public class ConstructorUnit extends ProcedureUnit
{
	public ConstructorUnit(Signature theSignature, ClassUnit theDeclaringClazz, String nameAndSignature)
	{
		super(theSignature, theDeclaringClazz, nameAndSignature);
	}

}
