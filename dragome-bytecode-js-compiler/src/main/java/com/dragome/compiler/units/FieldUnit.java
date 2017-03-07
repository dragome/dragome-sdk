package com.dragome.compiler.units;

import java.io.IOException;
import java.io.Writer;

import com.dragome.compiler.type.Signature;
import com.dragome.compiler.utils.Log;

public class FieldUnit extends MemberUnit
{

	public FieldUnit(Signature theSignature, ClassUnit theDeclaringClazz)
	{
		super(theSignature, theDeclaringClazz);
	}

	public void write(int depth, Writer writer, String... annotationDefaultFound) throws IOException
	{
		if (getData() == null)
			return;
		Log.getLogger().debug(getIndent(depth) + getSignature());
		writer.write(getData());
	}

}
