package java.security;

public class AccessController
{
	private static AccessControlContext context= new AccessControlContext();

	public static AccessControlContext getContext()
	{
		return context;
	}

	public static <T> T doPrivileged(PrivilegedAction<T> action)
	{
		return action.run();
	}

	public static <T> T doPrivileged(PrivilegedAction<T> privilegedAction, AccessControlContext acc)
	{
		return privilegedAction.run();
	}
}
