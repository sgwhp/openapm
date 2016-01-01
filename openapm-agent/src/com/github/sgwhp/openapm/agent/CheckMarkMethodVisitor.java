package com.github.sgwhp.openapm.agent;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Created by wuhongping on 15-11-18.
 */
public class CheckMarkMethodVisitor extends MethodVisitor {

    public CheckMarkMethodVisitor(MethodVisitor methodVisitor) {
        super(Opcodes.ASM5, methodVisitor);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, boolean b) {
        if(Type.getDescriptor(Transformed.class).equals(s)){
            throw new TransformedException();
        }
        return super.visitAnnotation(s, b);
    }
}
