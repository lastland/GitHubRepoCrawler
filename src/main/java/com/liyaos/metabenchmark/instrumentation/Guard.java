package com.liyaos.metabenchmark.instrumentation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ch.usi.dag.disl.annotation.GuardMethod;
import ch.usi.dag.disl.staticcontext.BytecodeStaticContext;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;
import ch.usi.dag.disl.Reflection.Class;
import ch.usi.dag.disl.Reflection.MissingClassException;
import ch.usi.dag.disl.guardcontext.ReflectionStaticContext;

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


	private static boolean __checkInterfaces (
			final Class leafClass, final Map <String, Boolean> resultCache,
			final Set <Type> interfacesToCheck, final Set <String> reportedMissing
			) {
		//
		// Check if we handled this class before.
		// If yes, return the cached result, otherwise perform the check.
		// If a class is missing, return false but do not cache the result.
		//
		final String className = leafClass.internalName ();

		final Boolean cachedResult = resultCache.get (className);
		if (cachedResult == null) {
			try {
				final boolean matches = __doCheckInterfaces (leafClass, interfacesToCheck);
				if (matches) {
					System.err.println ("matched: "+ leafClass.internalName ());
				}

				resultCache.put (className, matches);
				return matches;

			} catch (final MissingClassException cnle) {
				if (!reportedMissing.contains (cnle.classInternalName ())) {
					reportedMissing.add (cnle.classInternalName ());
					System.err.println ("warning: "+ cnle.getMessage ());
				}
				return false;
			}

		} else {
			return cachedResult;
		}
	}

	private static boolean __doCheckInterfaces (
			final Class leafClass, final Set <Type> interfaces
			) {
		//
		// Check the leaf class first, and continue up the inheritance
		// hierarchy until either a match is found, or we hit the roof.
		//
		Optional <Class> nextClass = Optional.of (leafClass);
		do {
			final Class checkClass = nextClass.get ();
			if (__implementsAnyOf (checkClass, interfaces)) {
				return true;
			}

			nextClass = checkClass.superClass ();
		} while (nextClass.isPresent ());

		// No match found.
		return false;
	}


	private static boolean __implementsAnyOf (
			final Class cl, final Set <Type> interfacesToCheck
			) {
		return cl.interfaceTypes ().anyMatch (interfacesToCheck::contains);
	}





//	private static boolean __checkClassesAndInterfaces(final String className, Map<String, Boolean> hashMapCache, final Set<String> classesToCheck) {
//
//		try {
//		
//		// Quick lookup in hash map
//		final Boolean alreadyMatched = hashMapCache.get(className);
//
//		if (alreadyMatched == null) { // Class not already processed
//			
//			boolean matches = false;
//			
//			
//			
//			Class<?> cl = null;
//			
//
//			// Check if this class is a specific class or implements a specific interface
//			
//				cl = Class.forName(className);					
//			
//								
//			
//
//			if (cl != null) {
//				matches = classesToCheck.contains(cl.getName());
//				if (!matches)
//					matches = __checkInterfacesOfClass(cl, classesToCheck);
//			}
//
//			// Now check also the class' superclasses, traversing the
//			// hierarchy
//			while (cl != null && !matches) {
//				cl = cl.getSuperclass();
//				if (cl!=null) {
//					matches = classesToCheck.contains(cl.getName());
//					if (!matches)
//						matches = __checkInterfacesOfClass(cl, classesToCheck);
//				}
//			}
//
//			 		
//			
//			hashMapCache.put(className, matches);
//			return matches;
//
//		}
//
//		else 
//			return alreadyMatched.booleanValue();
//		}
//		
//		
//		catch (Exception e) {
//			System.err.format("Error: Class %s not found!\n", className);		
//			return false;
//		}	
//
//	}


//private static boolean __checkInterfacesOfClass(final Class<?> cl, final Set<String> interfacesToCheck) {
//	if (cl == null)
//		return false;
//
//	final Class<?>[] interfaces = cl.getInterfaces();
//
//	for (int i = 0; i < interfaces.length; i++) {
//		String ifName = interfaces[i].getName();
//		if (interfacesToCheck.contains(ifName))
//			return true;
//	}
//
//	return false;
//
//}

//	static final class ThreadPoolOnly {
//
//		static Map<String, Boolean> toBeInstrumented = new HashMap<String, Boolean>();
//		static Set<String> classesToCheck = new HashSet<String>();
//
//		//TODO Remove array below, and use MainArguments.classToFilter
//		static final String[] classesToCheckArray = {"java.util.concurrent.Executor",
//			    "java.util.concurrent.ExecutorService", "java.util.concurrent.AbstractExecutorService",
//			    "java.util.concurrent.ThreadPoolExecutor", "java.util.concurrent.ForkJoinPool"
//				};
//		
//		static {
//			for (int i=0; i<classesToCheckArray.length; i++)			
//				classesToCheck.add(classesToCheckArray[i]);
//		}
//
//		@GuardMethod
//		public static boolean isClassToInstrument(final MethodStaticContext msc) {
//				
//			return __checkClassesAndInterfaces(msc.thisClassCanonicalName(), toBeInstrumented, classesToCheck);
//
//		}
//		
//	}


//



static final class ThreadPoolOnly {
	static Set <Type> interfacesToCheck = Arrays.asList (
			Executor.class, ExecutorService.class, AbstractExecutorService.class, ThreadPoolExecutor.class, ForkJoinPool.class
			).stream ().map (Type::getType).collect (Collectors.toSet ());


	static Map <String, Boolean> cachedResults = new HashMap <> ();

	static Set <String> reportedMissing = new HashSet <> ();


	@GuardMethod
	public static boolean isThreadPool (final ReflectionStaticContext rsc
			) {
		return __checkInterfaces (
				rsc.thisClass (), cachedResults, interfacesToCheck, reportedMissing
				);
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
