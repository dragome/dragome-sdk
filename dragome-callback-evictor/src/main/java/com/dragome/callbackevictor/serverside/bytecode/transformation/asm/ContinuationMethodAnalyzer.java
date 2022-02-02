/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.callbackevictor.serverside.bytecode.transformation.asm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

import com.dragome.commons.ContinueReflection;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class ContinuationMethodAnalyzer extends MethodNode implements Opcodes {

	public static boolean isAssignable(String className1, String className2) {
		try {
			if (className1.equals("java.lang.Object"))
				return true;

			CtClass ctClass1 = ClassPool.getDefault().get(className1);
			CtClass ctClass2 = ClassPool.getDefault().get(className2);

			if (ctClass1.isArray() ^ ctClass2.isArray())
				return false;

			if (ctClass1.isArray() && ctClass2.isArray()) {
				ctClass1 = ctClass1.getComponentType();
				ctClass2 = ctClass2.getComponentType();
			}

			return isSuper(ctClass1, ctClass2);

//			javassist.bytecode.analysis.Type typeA = javassist.bytecode.analysis.MultiType.get(ctClass1);
//			javassist.bytecode.analysis.Type typeB = javassist.bytecode.analysis.MultiType.get(ctClass2);
//
//			return typeB.isAssignableFrom(typeA);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * protected Class<?> getClass(Type t) { try { System.out.println("Resolving " +
	 * t.getClassName() + " using classloader: " +
	 * Thread.currentThread().getContextClassLoader()); if (t.getSort() ==
	 * Type.ARRAY) { return Class.forName(t.getDescriptor().replace('/', '.'), true,
	 * Thread.currentThread().getContextClassLoader()); } //return
	 * Class.forName(t.getClassName(), true,
	 * Thread.currentThread().getContextClassLoader()); return
	 * Class.forName(t.getClassName(), true,
	 * Thread.currentThread().getContextClassLoader()); //for now try this. It has
	 * to use the current classloader } catch (ClassNotFoundException e) { throw new
	 * RuntimeException(e.toString()); } }
	 */

	private static boolean isSuper(CtClass ctClass1, CtClass ctClass2) throws NotFoundException {
		if (ctClass1.equals(ctClass2))
			return true;

		CtClass superclass2 = ctClass2.getSuperclass();
		if (superclass2 != null) {
			if (isSuper(ctClass1, superclass2))
				return true;
		}

		List<CtClass> interfaces2 = Arrays.asList(ctClass2.getInterfaces());

		for (CtClass ctClass : interfaces2) {
			if (isSuper(ctClass1, ctClass))
				return true;
		}

		if (ctClass1.isInterface()) {
			ctClass1 = ClassPool.getDefault().get("java.lang.Object");
			return isSuper(ctClass1, ctClass2);
		}

		return false;
	}

	public class MyVerifier extends SimpleVerifier {
		MyVerifier() {
			super(Opcodes.ASM6, null, null, Collections.EMPTY_LIST, false);
			if (_useLoader != null) {
				setClassLoader(_useLoader);
				// Thread.currentThread().setContextClassLoader(_useLoader);
			}
		}

		protected Class<?> getClass(Type t) {
			try {
				if (t.getSort() == Type.ARRAY) {
					return Class.forName(t.getDescriptor().replace('/', '.'), true,
							Thread.currentThread().getContextClassLoader());
				}
				return Class.forName(t.getClassName(), true, Thread.currentThread().getContextClassLoader());
				// return Class.forName(t.getClassName()); //for now try this. It has to use the
				// current classloader
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e.toString());
			}
		}

		@Override
		protected boolean isAssignableFrom(Type type1, Type type2) {
//			boolean assignableFrom = super.isAssignableFrom(type1, type2);
			String className1 = type1.getClassName();
			String className2 = type2.getClassName();

			boolean assignable = isAssignable(className1, className2);

//			if (assignableFrom != assignable)
//				System.out.println("error!");

			return assignable;
//			return super.isAssignableFrom(type1, type2);
		}

		@Override
		protected boolean isInterface(Type type) {

			try {
				String className1 = type.getClassName();
				CtClass ctClass1 = ClassPool.getDefault().get(className1);
				return ctClass1.isInterface();
			} catch (NotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		protected Type getSuperClass(final Type type) {

			try {
				String className1 = type.getClassName();
				CtClass ctClass1 = ClassPool.getDefault().get(className1);
				CtClass superclass = ctClass1.getSuperclass();
				if (superclass == null)
					return null;

				StringBuilder stringBuilder = new StringBuilder();
				String name = superclass.getName();
				int nameLength = name.length();
				for (int i = 0; i < nameLength; ++i) {
					char car = name.charAt(i);
					stringBuilder.append(car == '.' ? '/' : car);
				}

				Type objectType = Type.getObjectType(stringBuilder.toString());
				return objectType;
			} catch (NotFoundException e) {
				throw new RuntimeException(e);
			}
		}

	}

	public class MyVariables {
		private int _methodObject;
		private int _object;
		private int _args;

		MyVariables(int m, int o, int a) {
			_methodObject = m;
			_object = o;
			_args = a;
		}

		public int methodVar() {
			return _methodObject;
		}

		public int objectVar() {
			return _object;
		}

		public int argsVar() {
			return _args;
		}
	}

	protected final String className;
	protected final ClassVisitor cv;
	protected final MethodVisitor mv;

	protected final List<Label> labels = new ArrayList<Label>();
	protected final List<MethodInsnNode> nodes = new ArrayList<MethodInsnNode>();
	protected final List<MethodInsnNode> methods = new ArrayList<MethodInsnNode>();

	// RS:
	private List _variables;
	protected final Map<MethodInsnNode, MyVariables> _reflectMapping = new HashMap<MethodInsnNode, MyVariables>();
	protected boolean _continueReflection;
	// RS:

	protected Analyzer analyzer;
	public int stackRecorderVar;

	private ClassLoader _useLoader;

	public ContinuationMethodAnalyzer(String className, ClassVisitor cv, MethodVisitor mv, int access, String name,
			String desc, String signature, String[] exceptions) {
		this(className, cv, mv, access, name, desc, signature, exceptions, null);
	}

	public ContinuationMethodAnalyzer(String className, ClassVisitor cv, MethodVisitor mv, int access, String name,
			String desc, String signature, String[] exceptions, ClassLoader loader) {
		super(Opcodes.ASM5, access, name, desc, signature, exceptions);
		this.className = className;
		this.cv = cv;
		this.mv = mv;
		_useLoader = loader;

		// RS:
		this._variables = new ArrayList();
		_continueReflection = false;
	}

	public int getIndex(AbstractInsnNode node) {
		return instructions.indexOf(node);
	}

	// RS:
	public void visitVarInsn(int opcode, int var) {
		super.visitVarInsn(opcode, var);
		if (opcode == ALOAD) {
			// store it for reuse for Method.invoke
			_variables.add(var);
		}
	}

	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		String chk = Type.getDescriptor(ContinueReflection.class);
		if (desc.startsWith(chk))
			_continueReflection = true;

		return super.visitAnnotation(desc, visible);
	}

	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		MethodInsnNode mnode = new MethodInsnNode(opcode, owner, name, desc, itf);
		if (opcode == INVOKESPECIAL || "<init>".equals(name)) {
			methods.add(mnode);
		}
		if (needsFrameGuard(opcode, owner, name, desc) /* && transformer.inScope(owner, name) */) {
			Label label = new Label();
			super.visitLabel(label);
			LabelNode labelNode = new LabelNode(label);
			instructions.add(labelNode);
			labels.add(label);
			nodes.add(mnode);

			// RS:
			if (_continueReflection && name.equals("invoke") && owner.startsWith("java/lang/reflect/Method")) {
				int mthd = ((Integer) _variables.get(_variables.size() - 3)).intValue();
				int obj = ((Integer) _variables.get(_variables.size() - 2)).intValue();
				int args = ((Integer) _variables.get(_variables.size() - 1)).intValue();
				MyVariables vars = new MyVariables(mthd, obj, args);
				_reflectMapping.put(mnode, vars);
			}
			// RS:
		}
		instructions.add(mnode);
	}

	public void visitEnd() {
		if (instructions.size() == 0 || labels.size() == 0) {
			accept(mv);
			return;
		}

		/*
		 * { TraceMethodVisitor mv = new TraceMethodVisitor(); System.err.println(name +
		 * desc); for (int j = 0; j < instructions.size(); ++j) { ((AbstractInsnNode)
		 * instructions.get(j)).accept(mv); System.err.print("   " + mv.text.get(j)); //
		 * mv.text.get(j)); } System.err.println(); }
		 */

		this.stackRecorderVar = maxLocals;
		try {
			moveNew();

			// analyzer = new Analyzer(new BasicVerifier());
			/*
			 * analyzer = new Analyzer(new SimpleVerifier() {
			 * 
			 * protected Class<?> getClass(Type t) { try { ClassLoader loader =
			 * Thread.currentThread().getContextClassLoader(); if (_useLoader != null)
			 * loader = _useLoader; if (t.getSort() == Type.ARRAY) { return
			 * Class.forName(t.getDescriptor().replace('/', '.'), true, loader); } //return
			 * Class.forName(t.getClassName(), true,
			 * Thread.currentThread().getContextClassLoader()); return
			 * Class.forName(t.getClassName(), loader); //for now try this. It has to use
			 * the current classloader } catch (ClassNotFoundException e) { throw new
			 * RuntimeException(e.toString()); } }
			 * 
			 * 
			 * })
			 */
			analyzer = new Analyzer(new MyVerifier()) {

				protected Frame newFrame(final int nLocals, final int nStack) {
					return new MonitoringFrame(nLocals, nStack);
				}

				protected Frame newFrame(final Frame src) {
					return new MonitoringFrame(src);
				}

				public Frame[] analyze(final String owner, final MethodNode m) throws AnalyzerException {
					// System.out.println("Analyze:
					// "+owner+"|"+m.name+"|"+m.signature+"|"+m.tryCatchBlocks);
					final Frame[] frames = super.analyze(owner, m);
					for (int i = 0; i < m.instructions.size(); i++) {
						int opcode = m.instructions.get(i).getOpcode();
						if (opcode == MONITORENTER || opcode == MONITOREXIT) {
							// System.out.println(i);
						}
					}
					return frames;
				}
			};

			analyzer.analyze(className, this);
			accept(new ContinuationMethodAdapter(this));

			/*
			 * { TraceMethodVisitor mv = new TraceMethodVisitor();
			 * System.err.println("=================");
			 * 
			 * System.err.println(name + desc); for (int j = 0; j < instructions.size();
			 * ++j) { ((AbstractInsnNode) instructions.get(j)).accept(mv);
			 * System.err.print("   " + mv.text.get(j)); // mv.text.get(j)); }
			 * System.err.println(); }
			 */

		} catch (AnalyzerException ex) {
			// TODO log the error or fail?
			ex.printStackTrace();
			accept(mv);

		}
	}

	@SuppressWarnings("unchecked")
	void moveNew() throws AnalyzerException {
		SourceInterpreter i = new SourceInterpreter();
		Analyzer a = new Analyzer(i);
		a.analyze(className, this);

		final HashMap<AbstractInsnNode, MethodInsnNode> movable = new HashMap<AbstractInsnNode, MethodInsnNode>();

		Frame[] frames = a.getFrames();
		for (int j = 0; j < methods.size(); j++) {
			MethodInsnNode mnode = methods.get(j);
			// require to move NEW instruction
			int n = instructions.indexOf(mnode);
			Frame f = frames[n];
			Type[] args = Type.getArgumentTypes(mnode.desc);

			SourceValue v = (SourceValue) f.getStack(f.getStackSize() - args.length - 1);
			Set<AbstractInsnNode> insns = v.insns;
			for (final AbstractInsnNode ins : insns) {
				if (ins.getOpcode() == NEW) {
					movable.put(ins, mnode);
				} else {
					// other known patterns
					int n1 = instructions.indexOf(ins);
					if (ins.getOpcode() == DUP) { // <init> with params
						AbstractInsnNode ins1 = instructions.get(n1 - 1);
						if (ins1.getOpcode() == NEW) {
							movable.put(ins1, mnode);
						}
					} else if (ins.getOpcode() == SWAP) { // in exception handler
						AbstractInsnNode ins1 = instructions.get(n1 - 1);
						AbstractInsnNode ins2 = instructions.get(n1 - 2);
						if (ins1.getOpcode() == DUP_X1 && ins2.getOpcode() == NEW) {
							movable.put(ins2, mnode);
						}
					}
				}
			}
		}

		int updateMaxStack = 0;
		for (final Map.Entry<AbstractInsnNode, MethodInsnNode> e : movable.entrySet()) {
			AbstractInsnNode node1 = e.getKey();
			int n1 = instructions.indexOf(node1);
			AbstractInsnNode node2 = instructions.get(n1 + 1);
			AbstractInsnNode node3 = instructions.get(n1 + 2);
			int producer = node2.getOpcode();

			instructions.remove(node1); // NEW
			boolean requireDup = false;
			if (producer == DUP) {
				instructions.remove(node2); // DUP
				requireDup = true;
			} else if (producer == DUP_X1) {
				instructions.remove(node2); // DUP_X1
				instructions.remove(node3); // SWAP
				requireDup = true;
			}

			MethodInsnNode mnode = (MethodInsnNode) e.getValue();
			AbstractInsnNode nm = mnode;

			int varOffset = stackRecorderVar + 1;
			Type[] args = Type.getArgumentTypes(mnode.desc);

			// optimizations for some common cases
			if (args.length == 0) {
				final InsnList doNew = new InsnList();
				doNew.add(node1); // NEW
				if (requireDup)
					doNew.add(new InsnNode(DUP));
				instructions.insertBefore(nm, doNew);
				nm = doNew.getLast();
				continue;
			}

			if (args.length == 1 && args[0].getSize() == 1) {
				final InsnList doNew = new InsnList();
				doNew.add(node1); // NEW
				if (requireDup) {
					doNew.add(new InsnNode(DUP));
					doNew.add(new InsnNode(DUP2_X1));
					doNew.add(new InsnNode(POP2));
					updateMaxStack = updateMaxStack < 2 ? 2 : updateMaxStack; // a two extra slots for temp values
				} else
					doNew.add(new InsnNode(SWAP));
				instructions.insertBefore(nm, doNew);
				nm = doNew.getLast();
				continue;
			}

			// TODO this one untested!
			if ((args.length == 1 && args[0].getSize() == 2)
					|| (args.length == 2 && args[0].getSize() == 1 && args[1].getSize() == 1)) {
				final InsnList doNew = new InsnList();
				doNew.add(node1); // NEW
				if (requireDup) {
					doNew.add(new InsnNode(DUP));
					doNew.add(new InsnNode(DUP2_X2));
					doNew.add(new InsnNode(POP2));
					updateMaxStack = updateMaxStack < 2 ? 2 : updateMaxStack; // a two extra slots for temp values
				} else {
					doNew.add(new InsnNode(DUP_X2));
					doNew.add(new InsnNode(POP));
					updateMaxStack = updateMaxStack < 1 ? 1 : updateMaxStack; // an extra slot for temp value
				}
				instructions.insertBefore(nm, doNew);
				nm = doNew.getLast();
				continue;
			}

			final InsnList doNew = new InsnList();
			// generic code using temporary locals
			// save stack
			for (int j = args.length - 1; j >= 0; j--) {
				Type type = args[j];

				doNew.add(new VarInsnNode(type.getOpcode(ISTORE), varOffset));
				varOffset += type.getSize();
			}
			if (varOffset > maxLocals) {
				maxLocals = varOffset;
			}

			doNew.add(node1); // NEW

			if (requireDup)
				doNew.add(new InsnNode(DUP));

			// restore stack
			for (int j = 0; j < args.length; j++) {
				Type type = args[j];
				varOffset -= type.getSize();

				doNew.add(new VarInsnNode(type.getOpcode(ILOAD), varOffset));

				// clean up store to avoid memory leak?
				if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
					updateMaxStack = updateMaxStack < 1 ? 1 : updateMaxStack; // an extra slot for ACONST_NULL

					doNew.add(new InsnNode(ACONST_NULL));

					doNew.add(new VarInsnNode(type.getOpcode(ISTORE), varOffset));
				}
			}
			instructions.insertBefore(nm, doNew);
			nm = doNew.getLast();
		}

		maxStack += updateMaxStack;
	}

	boolean needsFrameGuard(int opcode, String owner, String name, String desc) {
		/*
		 * TODO: need to customize a way enchancer skips classes/methods if
		 * (owner.startsWith("java/")) { System.out.println("SKIP:: " + owner + "." +
		 * name + desc); return false; }
		 */

		if (opcode == Opcodes.INVOKEINTERFACE || (opcode == Opcodes.INVOKESPECIAL && !"<init>".equals(name))
				|| opcode == Opcodes.INVOKESTATIC || opcode == Opcodes.INVOKEVIRTUAL) {
			return true;
		}
		return false;
	}

}