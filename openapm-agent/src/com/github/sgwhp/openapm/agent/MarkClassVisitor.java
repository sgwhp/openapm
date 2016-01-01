package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

import java.util.Map;

/**
 * Created by wuhongping on 15-11-23.
 */
public class MarkClassVisitor extends ClassVisitor {
    private Map<Method, MethodVisitorCreator> methodAdapterCreatorMap;
    private Log log;

    public MarkClassVisitor(ClassVisitor cv, Map<Method, MethodVisitorCreator> methodAdapterCreatorMap, Log log) {
        super(Opcodes.ASM5, cv);
        this.methodAdapterCreatorMap = methodAdapterCreatorMap;
        this.log = log;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] strings) {
        MethodVisitor mv =  super.visitMethod(access, name, desc, signature, strings);
        MethodVisitorCreator creator = methodAdapterCreatorMap.get(new Method(name, desc));
        if(creator != null){
            return new CheckMarkMethodVisitor(creator.create(mv, access, name, desc));
        }
        return mv;
    }
}
