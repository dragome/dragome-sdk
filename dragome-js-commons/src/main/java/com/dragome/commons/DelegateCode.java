package com.dragome.commons;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DelegateCode {
	boolean ignore() default false;
	String eval() default "";
}
