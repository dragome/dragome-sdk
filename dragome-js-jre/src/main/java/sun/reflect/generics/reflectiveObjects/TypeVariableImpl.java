package sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class TypeVariableImpl<D extends GenericDeclaration> implements TypeVariable<D>
{
	private Class<?> declaringClass;
	private String genericSignature;

	public TypeVariableImpl(Class<?> declaringClass, String genericSignature)
	{
		this.declaringClass= declaringClass;
		this.genericSignature= genericSignature;
	}

	public Type[] getBounds()
	{
		return new Type[] { Object.class };
	}

	public D getGenericDeclaration()
	{
		return (D) new GenericDeclaration()
		{
			public TypeVariable<?>[] getTypeParameters()
			{
				return null;
			}
		};
	}

	public String getName()
	{
		return genericSignature;
	}
}
