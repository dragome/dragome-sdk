package java.lang.annotation;

/**
 * Indicates the kinds of program element to which an annotation type
 * is applicable.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Target
{
	ElementType[] value();
}
