package com.github.sgwhp.openapm.agent.visitor;

import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.*;

/**
 * Created by wuhongping on 15-11-23.
 */
public class InitContextClassVisitor extends ClassVisitor {
    private final TransformContext context;
    private final Log log;

    public InitContextClassVisitor(TransformContext context, Log log) {
        super(Opcodes.ASM5);
        this.context = context;
        this.log = log;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
    {
        this.context.setClassName(name);
        this.context.setSuperClassName(superName);
    }
}
