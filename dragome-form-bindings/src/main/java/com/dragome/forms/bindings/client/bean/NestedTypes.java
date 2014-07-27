package com.dragome.forms.bindings.client.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be added to {@link com.pietschy.gwt.pectin.client.bean.BeanModelProvider} declarations
 * to let the generator know types that should be exposed as nested beans.  If the generator
 * finds a property of any of the specified types it will generate the appropriate nested accessor.
 * <p>
 * For example, the following usage will expose all nested properties that return an Address instance.
 * <pre>
 * &#064;NestedTypes({Address.class})
 * private static class PersonProvider extends BeanModelProvider<Person>{}
 * </pre>
 * @see com.pietschy.gwt.pectin.client.bean.LimitPropertyDepth
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NestedTypes
{
	Class[] value();
}
