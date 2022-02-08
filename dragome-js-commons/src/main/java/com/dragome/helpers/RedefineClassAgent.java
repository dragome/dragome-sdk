package com.dragome.helpers;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

import com.sun.tools.attach.VirtualMachine;

/**
 * Packages everything necessary to be able to redefine a class using
 * {@link Instrumentation} as provided by Java 1.6 or later. Class redefinition
 * is the act of replacing a class' bytecode at runtime, after that class has
 * already been loaded.
 * <p>
 * The scheme employed by this class uses an agent (defined by this class) that,
 * when loaded into the JVM, provides an instance of {@link Instrumentation}
 * which in turn provides a method to redefine classes.
 * <p>
 * Users of this class only need to call
 * {@link #redefineClasses(ClassDefinition...)}. The agent stuff will be done
 * automatically (and lazily).
 * <p>
 * Note that classes cannot be arbitrarily redefined. The new version must
 * retain the same schema; methods and fields cannot be added or removed. In
 * practice this means that method bodies can be changed.
 * <p>
 * Note that this is a replacement for javassist's {@code HotSwapper}.
 * {@code HotSwapper} depends on the debug agent to perform the hotswap. That
 * agent is available since Java 1.3, but the JVM must be started with the agent
 * enabled, and the agent often fails to perform the swap if the machine is
 * under heavy load. This class is both cleaner and more reliable.
 *
 * @see Instrumentation#redefineClasses(ClassDefinition...)
 *
 * @author Adam Lugowski
 */
public class RedefineClassAgent {
	/**
	 * Use the Java logger to avoid any references to anything not supplied by the
	 * JVM. This avoids issues with classpath when compiling/loading this class as
	 * an agent.
	 */
	private static final Logger LOGGER = Logger.getLogger(RedefineClassAgent.class.getSimpleName());

	/**
	 * Populated when this class is loaded into the JVM as an agent (via
	 * {@link #ensureAgentLoaded()}.
	 */
	private static volatile Instrumentation instrumentation = null;

	/**
	 * How long to wait for the agent to load before giving up and assuming the load
	 * failed.
	 */
	private static final int AGENT_LOAD_WAIT_TIME_SEC = 2;

	/**
	 * Agent entry point. Do not call this directly.
	 * <p>
	 * This method is called by the JVM when this class is loaded as an agent.
	 * <p>
	 * Sets {@link #instrumentation} to {@code inst}, provided {@code inst} supports
	 * class redefinition.
	 *
	 * @param agentArgs ignored.
	 * @param inst      This is the reason this class exists.
	 *                  {@link Instrumentation} has the
	 *                  {@link Instrumentation#redefineClasses(ClassDefinition...)}
	 *                  method.
	 */
	public static void agentmain(String agentArgs, Instrumentation inst) {
		if (!inst.isRedefineClassesSupported()) {
			LOGGER.severe("Class redefinition not supported. Aborting.");
			return;
		}

		System.setProperty("instru", "ready");
		setInstrumentation(inst);
	}

	/**
	 * Attempts to redefine class bytecode.
	 * <p>
	 * On first call this method will attempt to load an agent into the JVM to
	 * obtain an instance of {@link Instrumentation}. This agent load can introduce
	 * a pause (in practice 1 to 2 seconds).
	 *
	 * @see Instrumentation#redefineClasses(ClassDefinition...)
	 *
	 * @param definitions classes to redefine.
	 * @throws UnmodifiableClassException as thrown by
	 *                                    {@link Instrumentation#redefineClasses(ClassDefinition...)}
	 * @throws ClassNotFoundException     as thrown by
	 *                                    {@link Instrumentation#redefineClasses(ClassDefinition...)}
	 * @throws FailedToLoadAgentException if agent either failed to load or if the
	 *                                    agent wasn't able to get an instance of
	 *                                    {@link Instrumentation} that allows class
	 *                                    redefinitions.
	 */
	public static void redefineClasses(ClassDefinition... definitions)
			throws UnmodifiableClassException, ClassNotFoundException {
		ensureAgentLoaded();
		getInstrumentation().redefineClasses(definitions);
	}
	
	public static void retransform(Class... definitions)
			throws UnmodifiableClassException, ClassNotFoundException {
		ensureAgentLoaded();
		getInstrumentation().retransformClasses(definitions);
	}

	/**
	 * Lazy loads the agent that populates {@link #instrumentation}. OK to call
	 * multiple times.
	 *
	 * @throws FailedToLoadAgentException if agent either failed to load or if the
	 *                                    agent wasn't able to get an instance of
	 *                                    {@link Instrumentation} that allows class
	 *                                    redefinitions.
	 */
	public static void ensureAgentLoaded() {
		new RedefineClassAgent().ensure();
	}

	public void ensure() {
		if (isReady()) {
			// already loaded
			return;
		}

		// load the agent
		try {
			File agentJar = AgentHelper.createAgentJarFile();

			// Loading an agent requires the PID of the JVM to load the agent to. Find out
			// our PID.
			String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
			String pid = nameOfRunningVM.substring(0, nameOfRunningVM.indexOf('@'));

			// load the agent
			VirtualMachine vm = VirtualMachine.attach(pid);
			vm.loadAgent(agentJar.getAbsolutePath(), "");
			vm.detach();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// wait for the agent to load
		for (int sec = 0; sec < AGENT_LOAD_WAIT_TIME_SEC; sec++) {
			if (isReady()) {
				// success!
				return;
			}

			try {
				LOGGER.info("Sleeping for 1 second while waiting for agent to load.");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException();
			}
		}

		// agent didn't load
		throw new RuntimeException();
	}

	private static boolean isReady() {
		return getInstrumentation()!= null;
	}

	public static Instrumentation getInstrumentation() {
		return instrumentation;
	}

	public static void setInstrumentation(Instrumentation instrumentation) {
		RedefineClassAgent.instrumentation = instrumentation;
	}

}