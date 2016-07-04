package com.dragome.compiler.units;

import com.dragome.compiler.type.Signature;

public class MethodUnit extends ProcedureUnit
{
	public MethodUnit(Signature theSignature, ClassUnit theDeclaringClazz, String nameAndSignature)
	{
		super(theSignature, theDeclaringClazz, nameAndSignature);
	}

	@Override
	public int hashCode()
	{
		final int prime= 31;
		int result= 1;
		result= prime * result + ((getSignature().toString() == null) ? 0 : getSignature().toString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodUnit other= (MethodUnit) obj;
		if (getSignature().toString() == null)
		{
			if (other.getSignature().toString() != null)
				return false;
		}
		else if (!getSignature().toString().equals(other.getSignature().toString()))
			return false;
		return true;
	}

}
