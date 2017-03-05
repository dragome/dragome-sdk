package java.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.dragome.commons.compiler.annotations.AnnotationsHelper;
import com.dragome.commons.compiler.annotations.AnnotationsHelper.AnnotationContainer.AnnotationEntry;
import com.dragome.commons.javascript.ScriptHelper;

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

	public Object invoke(Object proxy, Method method, Object[] args) throws java.lang.ClassNotFoundException
	{
		ScriptHelper.put("method", method, this);
		String name= method.getName();
		if (name.equals("toString"))
		{
			return clazz.toString();
		}
		else if (name.equals("annotationType"))
		{
			return annotationClass;
		}
		else
		{
			List<AnnotationEntry> annotationEntries= new ArrayList<>(AnnotationsHelper.getAnnotationsByType(annotationClass).getEntries());

			for (AnnotationEntry annotationEntry : annotationEntries)
			{
				String key= getAnnotationKey(fieldName, parameterIndex, methodName, name);

				if (annotationEntry.getType().equals(clazz) && annotationEntry.getAnnotationKey().startsWith(key))
				{
					java.lang.String annotationValue= annotationEntry.getAnnotationValue();
					return convertResult(method.getReturnType(), annotationValue);
				}
			}

			String result= (String) ScriptHelper.eval("this.$$$annotationClass___java_lang_Class.$$$nativeClass___java_lang_Object.$$members[method.$$$signature___java_lang_String].apply()", this);
			return convertResult(method.getReturnType(), result);
		}
	}

	private Object convertResult(Class<?> type, String annotationValue) throws java.lang.ClassNotFoundException
	{
		if (type.isArray())
		{
			List<Object> result= new ArrayList<>();

			Class<?> componentType= type.getComponentType();
			String[] items= annotationValue.replace("{", "").replace("}", "").split(",");
			for (String item : items)
			{
				Object convertResult= convertResult(componentType, item);
				result.add(convertResult);
			}

			return result.toArray();
		}
		else if (type.equals(Boolean.class) || type.equals(boolean.class))
			return Boolean.parseBoolean(annotationValue);
		else if (type.equals(Class.class))
			return Class.forName(annotationValue.substring(1, annotationValue.length() - 1).replace("/", "."));
		else if (type.equals(Integer.class) || type.equals(int.class))
			return Integer.parseInt(annotationValue);
		else if (type.equals(Double.class) || type.equals(double.class))
			return Double.parseDouble(annotationValue);
		else
			return annotationValue;
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