package com.dragome.forms.bindings.client.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be added to {@link BeanModelProvider} declarations
 * to let tell the rebind process know how deep to traverse recursive
 * property paths.  A recursive property is one that returns another property
 * the same type as itself either as a directed descendant or as a descendant
 * of another property (at any depth).
 * <p>
 * As example you may have an employee that has a manager who is also an employee
 * and so on up the chain.  e.g. <code>employee.getManager().getManager()</code> etc.
 * Since such a path is infinitely recursive Pectin needs to be given a limit for the depth
 * that the code generation will generate (since valid property paths are computed at compile time).
 * <p>
 * The follow example will limit the maximum depth of any property paths to 3.  So this would limit
 * the manager example above to <code>provider.getValueModel("manager.manager.manager", Employee.class)</code>
 * <pre>
 * &#064;NestedTypes({Employee.class})
 * &#064;LimitPropertyDepth(3)
 * public static class EmployeeProvider extends BeanModelProvider&lt;Employee&gt;{}
 * </pre>
 * <p>
 * <b>NOTE:</b> This annotation is only required if the provider has nested types and the bean has recursive paths.  Pectin will
 * throw an exception during the rebind process if this annotation is required but has not been specified. (I.e. if you're not getting
 * the exception then you don't need the annotation).
 * <p>
 * <b>NOTE:</b> You don't need to be exact with depth as the optimisation process will strip any unused paths.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LimitPropertyDepth
{
	int value();
}
