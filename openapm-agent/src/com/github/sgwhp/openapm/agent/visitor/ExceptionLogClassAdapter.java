package com.github.sgwhp.openapm.agent.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by wuhongping on 15-12-4.
 */
public class ExceptionLogClassAdapter extends ClassVisitor {
    private TransformContext context;

    public ExceptionLogClassAdapter(ClassVisitor classVisitor, TransformContext context) {
        super(Opcodes.ASM5, classVisitor);
        this.context = context;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new ExceptionLogMethodAdapter(context
                , super.visitMethod(access, name, desc, signature, exceptions), access, name, desc);
    }
}
