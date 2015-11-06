package com.liyaos.metabenchmark.instrumentation;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import ch.usi.dag.disl.annotation.GuardMethod;
import ch.usi.dag.disl.staticcontext.BytecodeStaticContext;


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
