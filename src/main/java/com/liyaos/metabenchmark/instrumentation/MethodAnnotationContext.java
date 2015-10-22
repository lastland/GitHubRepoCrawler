package com.liyaos.metabenchmark.instrumentation;

import java.util.HashSet;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

import ch.usi.dag.disl.staticcontext.MethodStaticContext;

public class MethodAnnotationContext extends MethodStaticContext {

    public boolean isTest() {
        MethodNode methodNode = staticContextData.getMethodNode();
        HashSet<AnnotationNode> annos = new HashSet<>();
        if (methodNode.invisibleAnnotations != null) {
            annos.addAll(methodNode.invisibleAnnotations);
        }
        if (methodNode.visibleAnnotations != null) {
            annos.addAll(methodNode.visibleAnnotations);
        }
        for (AnnotationNode annotation : annos) {
            if ("Lorg/junit/Test;".equals(annotation.desc)) {
                return true;
            }
        }
        return false;
    }

}
