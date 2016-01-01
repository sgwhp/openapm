package com.github.sgwhp.openapm.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

/**
 * Created by wuhongping on 15-11-18.
 */
public class ProcessBuilderMethodVisitor extends MarkMethodVisitor {

    protected ProcessBuilderMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(methodVisitor, access, name, desc);
    }

    /**
     * 调用ProcessBuilderInvocationHandler#invoke
     */
    @Override
    protected void onMethodEnter(){
        invocationBuilder.loadInvocationDispatcher()
                .loadInvocationDispatcherKey(TransformAgent.genDispatcherKey("java/lang/ProcessBuilder", methodName))
                .loadArray(new Runnable[] {() -> {
                    loadThis();
                    invokeVirtual(Type.getObjectType("java/lang/ProcessBuilder")
                            , new Method("command", "()Ljava/util/List;"));
                }}).invokeDispatcher();
    }
}
