package com.liyaos.metabenchmark.instrumentation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ch.usi.dag.disl.annotation.GuardMethod;
import ch.usi.dag.disl.staticcontext.BytecodeStaticContext;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;

import com.liyaos.metabenchmark.MainArguments;

/**
 * Utility class containing assorted guards for the instrumentation.
 *
 * @author Lubomir Bulej
 * @author Aibek Sarimbekov
 */
final class Guard {

	private Guard () {
		// prevent instantiation from outside
	}


	private static boolean __checkClassesAndInterfaces(final String className, Map<String, Boolean> hashMapCache, final Set<String> classesToCheck) {

		try {
		
		// Quick lookup in hash map
		final Boolean alreadyMatched = hashMapCache.get(className);

		if (alreadyMatched == null) { // Class not already processed
			
			boolean matches = false;
			
			
			
			Class<?> cl = null;
			

			// Check if this class is a specific class or implements a specific interface
			
				cl = Class.forName(className);					
			
								
			

			if (cl != null) {
				matches = classesToCheck.contains(cl.getName());
				if (!matches)
					matches = __checkInterfacesOfClass(cl, classesToCheck);
			}

			// Now check also the class' superclasses, traversing the
			// hierarchy
			while (cl != null && !matches) {
				cl = cl.getSuperclass();
				if (cl!=null) {
					matches = classesToCheck.contains(cl.getName());
					if (!matches)
						matches = __checkInterfacesOfClass(cl, classesToCheck);
				}
			}

			 		
			
			hashMapCache.put(className, matches);
			return matches;

		}

		else 
			return alreadyMatched.booleanValue();
		}
		
		
		catch (Exception e) {
			System.err.format("Error: Class %s not found!\n", className);		
			return false;
		}	

	}


	private static boolean __checkInterfacesOfClass(final Class<?> cl, final Set<String> interfacesToCheck) {
		if (cl == null)
			return false;

		final Class<?>[] interfaces = cl.getInterfaces();

		for (int i = 0; i < interfaces.length; i++) {
			String ifName = interfaces[i].getName();
			if (interfacesToCheck.contains(ifName))
				return true;
		}

		return false;

	}

	static final class ClassToInstrumentOnly {

		static Map<String, Boolean> toBeInstrumented = new HashMap<String, Boolean>();
		static Set<String> classesToCheck = new HashSet<String>();

		//TODO Remove array below, and use MainArguments.classToFilter
		static final String[] classesToCheckArray = {"java.util.concurrent.Executor",
			    "java.util.concurrent.ExecutorService", "java.util.concurrent.AbstractExecutorService",
			    "java.util.concurrent.ThreadPoolExecutor", "java.util.concurrent.ForkJoinPool"
				};
		
		static {
			for (int i=0; i<classesToCheckArray.length; i++)			
				classesToCheck.add(classesToCheckArray[i]);
		}

		@GuardMethod
		public static boolean isClassToInstrument(final MethodStaticContext msc) {
				
			return __checkClassesAndInterfaces(msc.thisClassCanonicalName(), toBeInstrumented, classesToCheck);

		}
	}




	static final class SinglePrimitiveArrayAllocations {
		@GuardMethod
		public static boolean isApplicable (final BytecodeStaticContext bsc, final MultiArrayStaticContext masc) {
			final int opcode = bsc.getBytecodeNumber ();
			final boolean isNewArray = (opcode == Opcodes.NEWARRAY);
			final boolean isMultiANewArray = (opcode == Opcodes.MULTIANEWARRAY);

			return isNewArray || (
					isMultiANewArray && __isOneDimensionalPrimitiveMultiArray (
							masc.getDimensions (), masc.getElementTypeDescriptor ()
							)
					);
		}

		private static boolean __isOneDimensionalPrimitiveMultiArray (
				final int dimensionCount, final String elementTypeDescriptor
				) {
			return dimensionCount == 1 && ! __typeIsReference (elementTypeDescriptor);
		}
	}


	static final class SingleReferenceArrayAllocations {
		@GuardMethod
		public static boolean isApplicable (final BytecodeStaticContext bsc, final MultiArrayStaticContext masc) {
			final int opcode = bsc.getBytecodeNumber ();
			final boolean isANewArray = (opcode == Opcodes.ANEWARRAY);
			final boolean isMultiANewArray = (opcode == Opcodes.MULTIANEWARRAY);

			return isANewArray || (
					isMultiANewArray && __isOneDimensionalReferenceMultiArray (
							masc.getDimensions (), masc.getElementTypeDescriptor ()
							)
					);
		}

		private static boolean __isOneDimensionalReferenceMultiArray (
				final int dimensionCount, final String elementTypeDescriptor
				) {
			return dimensionCount == 1 && __typeIsReference (elementTypeDescriptor);
		}
	}

	//

	static final class MultiPrimitiveArrayAllocations {
		@GuardMethod
		public static boolean isApplicable (final BytecodeStaticContext bsc, final MultiArrayStaticContext masc) {
			final boolean isMultiArray = (bsc.getBytecodeNumber () == Opcodes.MULTIANEWARRAY);

			return isMultiArray && __isMultiDimensionalPrimitiveMultiArray (
					masc.getDimensions (), masc.getElementTypeDescriptor ()
					);
		}

		private static boolean __isMultiDimensionalPrimitiveMultiArray (
				final int dimensionCount, final String elementTypeDescriptor
				) {
			return dimensionCount > 1 && ! __typeIsReference (elementTypeDescriptor);
		}
	}


	static final class MultiReferenceArrayAllocations {
		@GuardMethod
		public static boolean isApplicable (final BytecodeStaticContext bsc, final MultiArrayStaticContext masc) {
			final boolean isMultiArray = (bsc.getBytecodeNumber () == Opcodes.MULTIANEWARRAY);

			return isMultiArray && __isMultiDimensionalReferenceMultiArray (
					masc.getDimensions (), masc.getElementTypeDescriptor ()
					);
		}

		private static boolean __isMultiDimensionalReferenceMultiArray (
				final int dimensionCount, final String elementTypeDescriptor
				) {
			return dimensionCount > 1 && __typeIsReference (elementTypeDescriptor);
		}
	}

	static final class TwoPrimitiveArrayAllocations {
		@GuardMethod
		public static boolean isApplicable (final BytecodeStaticContext bsc, final MultiArrayStaticContext masc) {
			final boolean isMultiArray = (bsc.getBytecodeNumber () == Opcodes.MULTIANEWARRAY);

			return isMultiArray && __isTwoDimensionalPrimitiveMultiArray (
					masc.getDimensions (), masc.getElementTypeDescriptor ()
					);
		}

		private static boolean __isTwoDimensionalPrimitiveMultiArray (
				final int dimensionCount, final String elementTypeDescriptor
				) {
			return dimensionCount == 2 && ! __typeIsReference (elementTypeDescriptor);
		}
	}


	static final class TwoReferenceArrayAllocations {
		@GuardMethod
		public static boolean isApplicable (final BytecodeStaticContext bsc, final MultiArrayStaticContext masc) {
			final boolean isMultiArray = (bsc.getBytecodeNumber () == Opcodes.MULTIANEWARRAY);

			return isMultiArray && __isTwoDimensionalReferenceMultiArray (
					masc.getDimensions (), masc.getElementTypeDescriptor ()
					);
		}

		private static boolean __isTwoDimensionalReferenceMultiArray (
				final int dimensionCount, final String elementTypeDescriptor
				) {
			return dimensionCount == 2 && __typeIsReference (elementTypeDescriptor);
		}
	}

	//
	// Utility methods
	//

	private static boolean __classIsObject (final String internalName) {
		return Type.getInternalName (Object.class).equals (internalName);
	}

	//

	private static boolean __typeIsReference (final String typeDescriptor) {
		return __typeIsReference (Type.getType (typeDescriptor));
	}

	private static boolean __typeIsReference (final Type type) {
		final int sort = type.getSort ();
		return sort == Type.OBJECT || sort == Type.ARRAY;
	}

}
