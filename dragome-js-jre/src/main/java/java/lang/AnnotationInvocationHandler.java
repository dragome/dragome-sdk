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
				String key= getAnnotationKey(fieldName, parameterIndex, methodName, name);

				if (annotationEntry.getType().equals(clazz) && annotationEntry.getAnnotationKey().startsWith(key))
					return annotationEntry.getAnnotationValue();
			}

			return null;
		}
	}

	public static String getAnnotationKey(String fieldName, Integer parameterIndex, String methodName, String name)
	{
		String parameterName= parameterIndex != null ? "arg" + parameterIndex.toString() : "";
		fieldName= fieldName != null ? fieldName : "";
		methodName= methodName != null ? methodName : "";
		String key= ":" + fieldName + ":" + methodName + ":" + parameterName + ":" + name;
		return key;
	}
}