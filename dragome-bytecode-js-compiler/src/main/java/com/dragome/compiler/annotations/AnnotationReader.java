package com.dragome.compiler.annotations;

import java.io.DataInputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.AttributeReader;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;

import com.dragome.compiler.Project;
import com.dragome.compiler.parser.Pass1;
import com.dragome.compiler.type.Signature;
import com.dragome.compiler.units.ClassUnit;

public class AnnotationReader implements AttributeReader
{

	private ClassUnit classUnit;

	public AnnotationReader(ClassUnit theClassUnit)
	{
		classUnit= theClassUnit;
	}

	public Attribute createAttribute(int name_index, int length, DataInputStream file, ConstantPool constantPool)
	{
		Map<String, String>[] annotations= null;

		try
		{
			int attCount= file.readUnsignedShort();
			annotations= new LinkedHashMap[attCount];
			for (int j= 0; j < attCount; j++)
			{
				Map<String, String> map= new LinkedHashMap<String, String>();
				annotations[j]= map;
				int nameIndex= file.readUnsignedShort();
				Constant constant= constantPool.getConstant(nameIndex);
				Signature signature= Project.getSingleton().getSignature(((ConstantUtf8) constant).getBytes());
				map.put("$signature", signature.toString());
				int fieldCount= file.readUnsignedShort();
				for (int i= 0; i < fieldCount; i++)
				{
					int fieldIndex= file.readUnsignedShort();
					constant= constantPool.getConstant(fieldIndex);
					String key= ((ConstantUtf8) constant).getBytes();
					byte b= file.readByte();
					fieldIndex= file.readUnsignedShort();
					constant= constantPool.getConstant(fieldIndex);
					String value= Pass1.constantToString(constant, constantPool);
					map.put(key, value);
				}
			}
		}
		catch (Exception e)
		{
		}

		AnnotationAttribute att= new AnnotationAttribute((byte) 0, name_index, length, constantPool);
		att.annotations= annotations;
		classUnit.annotations= annotations;
		return att;
	}
}
