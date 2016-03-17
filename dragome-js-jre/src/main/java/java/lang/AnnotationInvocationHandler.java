package java.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.dragome.commons.compiler.annotations.AnnotationsHelper;
import com.dragome.commons.compiler.annotations.AnnotationsHelper.AnnotationContainer.AnnotationEntry;

public class AnnotationInvocationHandler<T> implements InvocationHandler
{
	private Class<T> clazz;
	private Class<? extends Annotation> annotationClass;
	private String methodName;
	private Integer parameterIndex;
	private String fieldName;

	public AnnotationInvocationHandler(Class<T> class1, Class<? extends Annotation> annotationClass, String methodName, Integer parameterIndex, String fieldName)
	{
		this.clazz= class1;
		this.annotationClass= annotationClass;
		this.methodName= methodName;
		this.parameterIndex= parameterIndex;
		this.fieldName= fieldName;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
	{
		String name= method.getName();
		if (name.equals("toString"))
			return clazz.toString();
		else
		{
			List<AnnotationEntry> annotationEntries= new ArrayList<>(AnnotationsHelper.getAnnotationsByType(annotationClass).getEntries());

			for (AnnotationEntry annotationEntry : annotationEntries)
			{
				String key= method.getName();
				if (fieldName != null)
					key= "@" + fieldName + "/" + key;
				else
				{
					if (parameterIndex != null)
						key= "arg" + parameterIndex.toString() + "/" + key;
					if (methodName != null)
						key= methodName + "/" + key;
				}

				if (annotationEntry.getType().equals(clazz) && annotationEntry.getAnnotationKey().equals(key))
					return annotationEntry.getAnnotationValue();
			}

			return null;
		}
	}
}