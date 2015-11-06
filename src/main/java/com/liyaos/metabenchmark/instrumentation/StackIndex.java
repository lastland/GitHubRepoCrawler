package com.liyaos.metabenchmark.instrumentation;


/**
 * Represents a vocabulary of constants capturing the stack indices of operands
 * and results for various byte code instructions.
 *
 * @author Lubomir Bulej
 */
final class StackIndex {

    private StackIndex () {
        // prevent instantiation from outside
    }


    //
    // Top of the stack, sufficient for the result of most (if not all)
    // instructions producing a result.
    //

    static final int STACK_TOP = 0;


    //
    // Stack indices for operands before an instruction is executed
    //

    static final class Before {

        static final class SingleOpInsn {
            static final int VALUE = 0;
            static final int OBJECT_REF = 0;
        }


        static final class IF_ACMPx {
            static final int OBJECT_REF1 = 0;
            static final int OBJECT_REF2 = 1;
        }


        static final class xALOAD {
            static final int INDEX = 0;
            static final int ARRAY_REF = INDEX + 1;
        }


        static final class xASTORE {
            static final int VALUE = 0;
            static final int OBJECT_REF = VALUE;
            static final int INDEX = VALUE + 1;
            static final int ARRAY_REF = INDEX + 1;
        }


        static final class xNEWARRAY {
            static final class Single {
                static final int SIZE = 0;
            }
        }


        static final class PUTFIELD {
            static final int VALUE = 0;
            static final int OBJECT_REF = VALUE + 1;
        }

        static final class PUTSTATIC {
            static final int VALUE = 0;
        }

    }

}

