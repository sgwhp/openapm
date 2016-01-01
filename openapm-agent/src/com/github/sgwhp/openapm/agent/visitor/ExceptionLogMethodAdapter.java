package com.github.sgwhp.openapm.agent.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.util.*;

/**
 * Created by wuhongping on 15-12-4.
 */
public class ExceptionLogMethodAdapter extends AdviceAdapter {
    private TransformContext context;
    //记录所有目标exception的handle
    //key为handle，value是此handle对应的exception。
    //注：一个catch可能包含了多个exception，
    //如catch(IndexOutOfBoundsException | Exception e)
    private HashMap<Label, ArrayList<String>> matchedHandle = new HashMap<>();

    protected ExceptionLogMethodAdapter(TransformContext context
            , MethodVisitor methodVisitor, int access, String name, String desc) {
        super(Opcodes.ASM5, methodVisitor, access, name, desc);
        this.context = context;
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handle,
                                   String exception) {
        HashSet<String> targetException = context.getExceptions();
        if (exception != null && targetException.contains(exception)) {
            context.getLog().d("find exception " + exception);
            ArrayList<String> handles = matchedHandle.get(handle);
            if(handles == null) handles = new ArrayList<>();
            handles.add(exception);
            matchedHandle.put(handle, handles);
        }
        super.visitTryCatchBlock(start, end, handle, exception);
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        ArrayList<String> exceptions;
        if(label != null && (exceptions = matchedHandle.get(label)) != null){
            context.getLog().d("instrument exception");
            Label matched = new Label();
            Label end = new Label();
            //捕获的是目标exception的实例才进行处理
            final int N = exceptions.size() - 1;
            if (N >= 1) {
                for (int i = 0; i < N; i++) {
                    compareInstance(IFNE, exceptions.get(i), matched);
                }
            }
            compareInstance(IFEQ, exceptions.get(N), end);
            visitLabel(matched);
            dup();
            invokeStatic(Type.getObjectType("com/github/sgwhp/openapm/monitor/Monitor")
                    , new Method("pushException", "(Ljava/lang/Throwable;)V"));
            visitLabel(end);
            context.markModified();
        }
    }

    private void compareInstance(int mode, String type, Label to){
        dup();
        instanceOf(Type.getObjectType(type));
        visitJumpInsn(mode, to);
    }
}
