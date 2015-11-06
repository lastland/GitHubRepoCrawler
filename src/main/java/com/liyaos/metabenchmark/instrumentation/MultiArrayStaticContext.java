package com.liyaos.metabenchmark.instrumentation;

        import org.objectweb.asm.Type;
        import org.objectweb.asm.tree.MultiANewArrayInsnNode;

        import ch.usi.dag.disl.marker.BytecodeMarker;
        import ch.usi.dag.disl.staticcontext.AbstractStaticContext;


/**
 * Represents a static context for a multi-dimensional array allocation.
 * Provides access to the number of dimensions and the type descriptor of the
 * array being allocated.
 * <p>
 * <b>Note:</b> This context should only be used with the {@link BytecodeMarker}
 * triggering on the MULTIANEWARRAY instruction. If you are not sure whether the
 * context can be used, use the {@link #isValid()} method to check if the
 * context is valid.
 *
 * @author Aibek Sarimbekov
 * @author Lubomir Bulej
 */
final class MultiArrayStaticContext extends AbstractStaticContext {

    public MultiArrayStaticContext () {
        // invoked by DiSL
    }

    /**
     * @return {@code True} if the context is valid.
     */
    public boolean isValid () {
        return staticContextData.getRegionStart () instanceof MultiANewArrayInsnNode;
    }


    /**
     * @return The number of dimensions of the array being allocated.
     */
    public int getDimensions () {
        return __multiANewArrayInsnNode ().dims;
    }


    /**
     * @return The type descriptor of the array being allocated.
     */
    public String getDescriptor () {
        return __descriptor ();
    }


    /**
     * @return The type descriptor of the array's base element type.
     */
    public String getElementTypeDescriptor () {
        return Type.getType (__descriptor ()).getElementType ().getDescriptor ();
    }

    //

    private String __descriptor () {
        return __multiANewArrayInsnNode ().desc;
    }

    private MultiANewArrayInsnNode __multiANewArrayInsnNode () {
        //
        // This will throw an exception when used in a region that does not
        // start with a multi-dimensional array allocation instruction.
        //
        return (MultiANewArrayInsnNode) staticContextData.getRegionStart ();
    }

}
