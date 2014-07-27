package com.dragome.forms.bindings.client.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to mark a bean property as being a nested bean.  The annotation must
 * be placed on the getter of the parent bean.
 * <p>
 * The following will expose the address property as a nested bean.
 * <pre>
 * public class Person {
 *   &#064;NestedBean
 *   public Address getAddress() {return address;}
 * }
 * </pre>
 * As an alternative you can also the {@link com.pietschy.gwt.pectin.client.bean.NestedTypes}
 * annotation on you {@link com.pietschy.gwt.pectin.client.bean.BeanModelProvider}.
 *
 * @see com.pietschy.gwt.pectin.client.bean.NestedTypes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NestedBean
{
}
