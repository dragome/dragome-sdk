package java.lang.annotation;

/**
 * Annotation retention policy.
 */
public enum RetentionPolicy
{
	/**
	 * Annotations are to be discarded by the compiler.
	 */
	SOURCE,

	/**
	 * Annotations are to be recorded in the class file by the compiler
	 * but need not be retained by the VM at run time.
	 */
	CLASS,

	/**
	 * Annotations are to be recorded in the class file by the compiler and
	 * retained by the VM at run time.
	 */
	RUNTIME
}
