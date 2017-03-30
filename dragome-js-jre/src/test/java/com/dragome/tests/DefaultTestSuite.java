package com.dragome.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ StaticMembersTests.class, ReflectionAPITests.class, LambdaTests.class, ClosureTests.class,
		CallMethodTests.class, DefaultMethodsTests.class })
public class DefaultTestSuite extends TestSuite {
}
