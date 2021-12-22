package com.dragome.web.debugging.messages;

import java.io.IOException;
import java.lang.reflect.Method;

import org.nustaq.serialization.FSTBasicObjectSerializer;
import org.nustaq.serialization.FSTClazzInfo;
import org.nustaq.serialization.FSTClazzInfo.FSTFieldInfo;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

public class MethodSerializer extends FSTBasicObjectSerializer
{

	public void writeObject(FSTObjectOutput out, Object toWrite, FSTClazzInfo clzInfo, FSTFieldInfo referencedBy, int streamPosition) throws IOException
	{
		Method method= (Method) toWrite;
		
		out.writeStringUTF(method.getName());
	}

	public void readObject(FSTObjectInput in, Object toRead, FSTClazzInfo clzInfo, FSTFieldInfo referencedBy) throws Exception
	{
		// TODO Auto-generated method stub
		super.readObject(in, toRead, clzInfo, referencedBy);
	}
}