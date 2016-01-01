package com.github.sgwhp.openapm.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * Created by wuhongping on 15-11-23.
 */
public class DexerMainMethodVisitor extends MarkMethodVisitor {

    protected DexerMainMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc) {
        super(methodVisitor, access, name, desc);
    }

    @Override
    protected void onMethodEnter() {
        invocationBuilder.loadInvocationDispatcher()
                .loadInvocationDispatcherKey(TransformAgent.genDispatcherKey("com/android/dx/command/dexer/Main", methodName))
                .loadArgumentsArray(methodDesc)
                .invokeDispatcher(false);
        checkCast(Type.getType("[B"));
        storeArg(1);
    }
}
