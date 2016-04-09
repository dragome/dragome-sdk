/*******************************************************************************
 * Copyright (c) 2007 java2script.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zhou Renjian - initial API and implementation
 *******************************************************************************/

package java.lang;

import java.lang.Thread.State;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.locks.LockSupport;

/**
 * @author zhou renjian
 *
 * 2006-5-5
 */
public class Thread implements Runnable
{
	public enum State
	{
		/**
		 * Thread state for a thread which has not yet started.
		 */
		NEW,

		/**
		 * Thread state for a runnable thread.  A thread in the runnable
		 * state is executing in the Java virtual machine but it may
		 * be waiting for other resources from the operating system
		 * such as processor.
		 */
		RUNNABLE,

		/**
		 * Thread state for a thread blocked waiting for a monitor lock.
		 * A thread in the blocked state is waiting for a monitor lock
		 * to enter a synchronized block/method or
		 * reenter a synchronized block/method after calling
		 * {@link Object#wait() Object.wait}.
		 */
		BLOCKED,

		/**
		 * Thread state for a waiting thread.
		 * A thread is in the waiting state due to calling one of the
		 * following methods:
		 * <ul>
		 *   <li>{@link Object#wait() Object.wait} with no timeout</li>
		 *   <li>{@link #join() Thread.join} with no timeout</li>
		 *   <li>{@link LockSupport#park() LockSupport.park}</li>
		 * </ul>
		 *
		 * <p>A thread in the waiting state is waiting for another thread to
		 * perform a particular action.
		 *
		 * For example, a thread that has called <tt>Object.wait()</tt>
		 * on an object is waiting for another thread to call
		 * <tt>Object.notify()</tt> or <tt>Object.notifyAll()</tt> on
		 * that object. A thread that has called <tt>Thread.join()</tt>
		 * is waiting for a specified thread to terminate.
		 */
		WAITING,

		/**
		 * Thread state for a waiting thread with a specified waiting time.
		 * A thread is in the timed waiting state due to calling one of
		 * the following methods with a specified positive waiting time:
		 * <ul>
		 *   <li>{@link #sleep Thread.sleep}</li>
		 *   <li>{@link Object#wait(long) Object.wait} with timeout</li>
		 *   <li>{@link #join(long) Thread.join} with timeout</li>
		 *   <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
		 *   <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
		 * </ul>
		 */
		TIMED_WAITING,

		/**
		 * Thread state for a terminated thread.
		 * The thread has completed execution.
		 */
		TERMINATED;
	}

	/**
	 * Returns the state of this thread.
	 * This method is designed for use in monitoring of the system state,
	 * not for synchronization control.
	 *
	 * @return this thread's state.
	 * @since 1.5
	 */
	public State getState()
	{
		return State.RUNNABLE;
	}

	ThreadLocal.ThreadLocalMap inheritableThreadLocals= null;

	public interface UncaughtExceptionHandler
	{
		/**
		 * Method invoked when the given thread terminates due to the
		 * given uncaught exception.
		 * <p>Any exception thrown by this method will be ignored by the
		 * Java Virtual Machine.
		 * @param t the thread
		 * @param e the exception
		 */
		void uncaughtException(Thread t, Throwable e);
	}

	/**
	 * The minimum priority that a thread can have.
	 */
	public final static int MIN_PRIORITY= 1;

	/**
	  * The default priority that is assigned to a thread.
	  */
	public final static int NORM_PRIORITY= 5;

	/**
	 * The maximum priority that a thread can have.
	 */
	public final static int MAX_PRIORITY= 10;

	private static Thread J2S_THREAD= null;

	/**
	 * Returns a reference to the currently executing thread object.
	 *
	 * @return  the currently executing thread.
	 */
	public static Thread currentThread()
	{
		if (J2S_THREAD == null)
		{
			J2S_THREAD= new Thread();
		}
		return J2S_THREAD;
	}
	/**
	 * Causes the currently executing thread to sleep (temporarily cease
	 * execution) for the specified number of milliseconds. The thread
	 * does not lose ownership of any monitors.
	 *
	 * @param      millis   the length of time to sleep in milliseconds.
	 * @exception  InterruptedException if another thread has interrupted
	 *             the current thread.  The <i>interrupted status</i> of the
	 *             current thread is cleared when this exception is thrown.
	 * @see        java.lang.Object#notify()
	 *
	 * @j2sNative
	 * alert ("Thread.sleep is not implemented in Java2Script!");
	 */
	public static void sleep(long millis) throws InterruptedException
	{
		// ;
	}

	/* What will be run. */
	private Runnable target;

	private String name;
	private int priority;

	public Thread()
	{
	}

	/**
	 * Allocates a new <code>Thread</code> object. This constructor has
	 * the same effect as <code>Thread(null, target,</code>
	 * <i>gname</i><code>)</code>, where <i>gname</i> is
	 * a newly generated name. Automatically generated names are of the
	 * form <code>"Thread-"+</code><i>n</i>, where <i>n</i> is an integer.
	 *
	 * @param   target   the object whose <code>run</code> method is called.
	 * @see     java.lang.Thread#Thread(java.lang.ThreadGroup,
	 *          java.lang.Runnable, java.lang.String)
	 */
	public Thread(Runnable target)
	{
	}

	/**
	 * Allocates a new <code>Thread</code> object. This constructor has
	 * the same effect as <code>Thread(group, target,</code>
	 * <i>gname</i><code>)</code>, where <i>gname</i> is
	 * a newly generated name. Automatically generated names are of the
	 * form <code>"Thread-"+</code><i>n</i>, where <i>n</i> is an integer.
	 *
	 * @param      group    the thread group.
	 * @param      target   the object whose <code>run</code> method is called.
	 * @exception  SecurityException  if the current thread cannot create a
	 *             thread in the specified thread group.
	 * @see        java.lang.Thread#Thread(java.lang.ThreadGroup,
	 *             java.lang.Runnable, java.lang.String)
	 */

	/**
	 * Allocates a new <code>Thread</code> object. This constructor has
	 * the same effect as <code>Thread(null, null, name)</code>.
	 *
	 * @param   name   the name of the new thread.
	 * @see     java.lang.Thread#Thread(java.lang.ThreadGroup,
	 *          java.lang.Runnable, java.lang.String)
	 */
	public Thread(String name)
	{
	}

	/**
	 * Allocates a new <code>Thread</code> object. This constructor has
	 * the same effect as <code>Thread(group, null, name)</code>
	 *
	 * @param      group   the thread group.
	 * @param      name    the name of the new thread.
	 * @exception  SecurityException  if the current thread cannot create a
	 *               thread in the specified thread group.
	 * @see        java.lang.Thread#Thread(java.lang.ThreadGroup,
	 *          java.lang.Runnable, java.lang.String)
	 */

	/**
	 * Allocates a new <code>Thread</code> object. This constructor has
	 * the same effect as <code>Thread(null, target, name)</code>.
	 *
	 * @param   target   the object whose <code>run</code> method is called.
	 * @param   name     the name of the new thread.
	 * @see     java.lang.Thread#Thread(java.lang.ThreadGroup,
	 *          java.lang.Runnable, java.lang.String)
	 */
	public Thread(Runnable target, String name)
	{
	}

	/**
	 * Allocates a new <code>Thread</code> object so that it has
	 * <code>target</code> as its run object, has the specified
	 * <code>name</code> as its name, and belongs to the thread group
	 * referred to by <code>group</code>.
	 * <p>
	 * If <code>group</code> is <code>null</code> and there is a
	 * security manager, the group is determined by the security manager's
	 * <code>getThreadGroup</code> method. If <code>group</code> is
	 * <code>null</code> and there is not a security manager, or the
	 * security manager's <code>getThreadGroup</code> method returns
	 * <code>null</code>, the group is set to be the same ThreadGroup
	 * as the thread that is creating the new thread.
	 *
	 * <p>If there is a security manager, its <code>checkAccess</code>
	 * method is called with the ThreadGroup as its argument.
	 * This may result in a SecurityException.
	 * <p>
	 * If the <code>target</code> argument is not <code>null</code>, the
	 * <code>run</code> method of the <code>target</code> is called when
	 * this thread is started. If the target argument is
	 * <code>null</code>, this thread's <code>run</code> method is called
	 * when this thread is started.
	 * <p>
	 * The priority of the newly created thread is set equal to the
	 * priority of the thread creating it, that is, the currently running
	 * thread. The method <code>setPriority</code> may be used to
	 * change the priority to a new value.
	 * <p>
	 * The newly created thread is initially marked as being a daemon
	 * thread if and only if the thread creating it is currently marked
	 * as a daemon thread. The method <code>setDaemon </code> may be used
	 * to change whether or not a thread is a daemon.
	 *
	 * @param      group     the thread group.
	 * @param      target   the object whose <code>run</code> method is called.
	 * @param      name     the name of the new thread.
	 * @exception  SecurityException  if the current thread cannot create a
	 *               thread in the specified thread group.
	 * @see        java.lang.Runnable#run()
	 * @see        java.lang.Thread#run()
	 * @see        java.lang.Thread#setDaemon(boolean)
	 * @see        java.lang.Thread#setPriority(int)
	 * @see        java.lang.ThreadGroup#checkAccess()
	 * @see        SecurityManager#checkAccess
	 */

	/**
	 * Allocates a new <code>Thread</code> object so that it has
	 * <code>target</code> as its run object, has the specified
	 * <code>name</code> as its name, belongs to the thread group referred to
	 * by <code>group</code>, and has the specified <i>stack size</i>.
	 *
	 * <p>This constructor is identical to {@link
	 * #Thread(ThreadGroup,Runnable,String)} with the exception of the fact
	 * that it allows the thread stack size to be specified.  The stack size
	 * is the approximate number of bytes of address space that the virtual
	 * machine is to allocate for this thread's stack.  <b>The effect of the
	 * <tt>stackSize</tt> parameter, if any, is highly platform dependent.</b>
	 *
	 * <p>On some platforms, specifying a higher value for the
	 * <tt>stackSize</tt> parameter may allow a thread to achieve greater
	 * recursion depth before throwing a {@link StackOverflowError}.
	 * Similarly, specifying a lower value may allow a greater number of
	 * threads to exist concurrently without throwing an an {@link
	 * OutOfMemoryError} (or other internal error).  The details of
	 * the relationship between the value of the <tt>stackSize</tt> parameter
	 * and the maximum recursion depth and concurrency level are
	 * platform-dependent.  <b>On some platforms, the value of the
	 * <tt>stackSize</tt> parameter may have no effect whatsoever.</b>
	 *
	 * <p>The virtual machine is free to treat the <tt>stackSize</tt>
	 * parameter as a suggestion.  If the specified value is unreasonably low
	 * for the platform, the virtual machine may instead use some
	 * platform-specific minimum value; if the specified value is unreasonably
	 * high, the virtual machine may instead use some platform-specific
	 * maximum.  Likewise, the virtual machine is free to round the specified
	 * value up or down as it sees fit (or to ignore it completely).
	 *
	 * <p>Specifying a value of zero for the <tt>stackSize</tt> parameter will
	 * cause this constructor to behave exactly like the
	 * <tt>Thread(ThreadGroup, Runnable, String)</tt> constructor.
	 *
	 * <p><i>Due to the platform-dependent nature of the behavior of this
	 * constructor, extreme care should be exercised in its use.
	 * The thread stack size necessary to perform a given computation will
	 * likely vary from one JRE implementation to another.  In light of this
	 * variation, careful tuning of the stack size parameter may be required,
	 * and the tuning may need to be repeated for each JRE implementation on
	 * which an application is to run.</i>
	 *
	 * <p>Implementation note: Java platform implementers are encouraged to
	 * document their implementation's behavior with respect to the
	 * <tt>stackSize parameter</tt>.
	 *
	 * @param      group    the thread group.
	 * @param      target   the object whose <code>run</code> method is called.
	 * @param      name     the name of the new thread.
	 * @param      stackSize the desired stack size for the new thread, or
	 *             zero to indicate that this parameter is to be ignored.
	 * @exception  SecurityException  if the current thread cannot create a
	 *               thread in the specified thread group.
	 */

	/**
	 * Initialize a Thread.
	 *
	 * @param g the Thread group
	 * @param target the object whose run() method gets called
	 * @param name the name of the new Thread
	 * @param stackSize the desired stack size for the new thread, or
	 *        zero to indicate that this parameter is to be ignored.
	 */

	public Thread(ThreadGroup g, String name2)
	{
		// TODO Auto-generated constructor stub
	}
	/**
	 * Causes this thread to begin execution; the Java Virtual Machine
	 * calls the <code>run</code> method of this thread.
	 * <p>
	 * The result is that two threads are running concurrently: the
	 * current thread (which returns from the call to the
	 * <code>start</code> method) and the other thread (which executes its
	 * <code>run</code> method).
	 *
	 * @exception  IllegalThreadStateException  if the thread was already
	 *               started.
	 * @see        java.lang.Thread#run()
	 * @see        java.lang.Thread#stop()
	 *
	 * @j2sNative
	 * window.setTimeout ((function (runnable) {
	 * 	return function () {
	 * 		runnable.run ();
	 *	};
	 * }) (this), 0);
	 */
	public synchronized void start()
	{
		// ;
	}

	/**
	 * If this thread was constructed using a separate
	 * <code>Runnable</code> run object, then that
	 * <code>Runnable</code> object's <code>run</code> method is called;
	 * otherwise, this method does nothing and returns.
	 * <p>
	 * Subclasses of <code>Thread</code> should override this method.
	 *
	 * @see     java.lang.Thread#start()
	 * @see     java.lang.Thread#stop()
	 * @see     java.lang.Thread#Thread(java.lang.ThreadGroup,
	 *          java.lang.Runnable, java.lang.String)
	 * @see     java.lang.Runnable#run()
	 */
	public void run()
	{
		if (target != null)
		{
			target.run();
		}
	}

	/**
	 * Changes the priority of this thread.
	 * <p>
	 * First the <code>checkAccess</code> method of this thread is called
	 * with no arguments. This may result in throwing a
	 * <code>SecurityException</code>.
	 * <p>
	 * Otherwise, the priority of this thread is set to the smaller of
	 * the specified <code>newPriority</code> and the maximum permitted
	 * priority of the thread's thread group.
	 *
	 * @param newPriority priority to set this thread to
	 * @exception  IllegalArgumentException  If the priority is not in the
	 *               range <code>MIN_PRIORITY</code> to
	 *               <code>MAX_PRIORITY</code>.
	 * @exception  SecurityException  if the current thread cannot modify
	 *               this thread.
	 * @see        #getPriority
	 * @see        java.lang.Thread#checkAccess()
	 * @see        java.lang.Thread#getPriority()
	 * @see        java.lang.Thread#getThreadGroup()
	 * @see        java.lang.Thread#MAX_PRIORITY
	 * @see        java.lang.Thread#MIN_PRIORITY
	 * @see        java.lang.ThreadGroup#getMaxPriority()
	 */
	public final void setPriority(int newPriority)
	{
		if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY)
		{
			throw new IllegalArgumentException();
		}
		this.priority= newPriority;
	}

	/**
	 * Returns this thread's priority.
	 *
	 * @return  this thread's priority.
	 * @see     #setPriority
	 * @see     java.lang.Thread#setPriority(int)
	 */
	public final int getPriority()
	{
		return priority;
	}

	/**
	 * Changes the name of this thread to be equal to the argument
	 * <code>name</code>.
	 * <p>
	 * First the <code>checkAccess</code> method of this thread is called
	 * with no arguments. This may result in throwing a
	 * <code>SecurityException</code>.
	 *
	 * @param      name   the new name for this thread.
	 * @exception  SecurityException  if the current thread cannot modify this
	 *               thread.
	 * @see        #getName
	 * @see        java.lang.Thread#checkAccess()
	 * @see        java.lang.Thread#getName()
	 */
	public final void setName(String name)
	{
		this.name= name;
	}

	/**
	 * Returns this thread's name.
	 *
	 * @return  this thread's name.
	 * @see     #setName
	 * @see     java.lang.Thread#setName(java.lang.String)
	 */
	public final String getName()
	{
		return String.valueOf(name);
	}

	/**
	 * Returns the thread group to which this thread belongs.
	 * This method returns null if this thread has died
	 * (been stopped).
	 *
	 * @return  this thread's thread group.
	 */

	/**
	 * Returns a string representation of this thread, including the
	 * thread's name, priority, and thread group.
	 *
	 * @return  a string representation of this thread.
	 */
	public String toString()
	{
		return hashCode() + "";
	}
	public static boolean interrupted()
	{
		return false;
	}
	public StackTraceElement[] getStackTrace()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void interrupt()
	{
	}
	public void join(long ms, int ns)
	{
		// TODO Auto-generated method stub

	}
	public static void sleep(long ms, int ns)
	{
		// TODO Auto-generated method stub

	}
	public ClassLoader getContextClassLoader()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public void setContextClassLoader(ClassLoader last)
	{
		// TODO Auto-generated method stub

	}
	public ThreadGroup getThreadGroup()
	{
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isAlive()
	{
		// TODO Auto-generated method stub
		return false;
	}
	public void stop()
	{
		// TODO Auto-generated method stub

	}
	public void suspend()
	{
		// TODO Auto-generated method stub

	}
	public void resume()
	{
		// TODO Auto-generated method stub

	}
	public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
