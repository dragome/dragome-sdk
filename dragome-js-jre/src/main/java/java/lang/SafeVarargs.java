package java.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * A programmer assertion that the body of the annotated method or constructor does not perform potentially unsafe operations on its varargs parameter. Applying this annotation to a method or constructor suppresses unchecked warnings about a non-reifiable variable arity (vararg) type and suppresses unchecked warnings about parameterized array creation at call sites.
 * In addition to the usage restrictions imposed by its @Target meta-annotation, compilers are required to implement additional usage restrictions on this annotation type; it is a compile-time error if a method or constructor declaration is annotated with a @SafeVarargs annotation, and either:
 *     the declaration is a fixed arity method or constructor
 *     the declaration is a variable arity method that is neither static nor final. 
 * Compilers are encouraged to issue warnings when this annotation type is applied to a method or constructor declaration where:
 *     The variable arity parameter has a reifiable element type, which includes primitive types, Object, and String. (The unchecked warnings this annotation type suppresses already do not occur for a reifiable element type.)
 *     The body of the method or constructor declaration performs potentially unsafe operations, such as an assignment to an element of the variable arity parameter's array that generates an unchecked warning. Some unsafe operations do not trigger an unchecked warning. For example, the aliasing in
 * 
 *          @SafeVarargs // Not actually safe!
 *          static void m(List<String>... stringLists) {
 *            Object[] array = stringLists;
 *            List<Integer> tmpList = Arrays.asList(42);
 *            array[0] = tmpList; // Semantically invalid, but compiles without warnings
 *            String s = stringLists[0].get(0); // Oh no, ClassCastException at runtime!
 *          }
 *          
 *     leads to a ClassCastException at runtime.
 * 
 *     Future versions of the platform may mandate compiler errors for such unsafe operations. 
 * 
 * Since:
 *     1.7 
 *     
 *  read more  https://docs.oracle.com/javase/8/docs/api/java/lang/SafeVarargs.html
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD })
public @interface SafeVarargs
{
}
