package com.github.sgwhp.openapm.agent.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by wuhongping on 15-11-24.
 */
public class ContextClassVisitor extends ClassVisitor {
    private final TransformContext context;

    public ContextClassVisitor(ClassVisitor paramClassVisitor, TransformContext context) {
        super(Opcodes.ASM5, paramClassVisitor);
        this.context = context;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        context.setClassName(name);
        context.setSuperClassName(superName);
        super.visit(version, access, name, signature, superName, interfaces);
    }
}
