package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.Method;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuhongping on 15-11-18.
 */
public abstract class ClassAdapterCreator {
    protected Log log;

    public ClassAdapterCreator(Log log){
        this.log = log;
    }

    abstract ClassVisitor create(ClassVisitor classVisitor);

    public static class ProcessBuilderClassAdapterCreator extends ClassAdapterCreator {

        public ProcessBuilderClassAdapterCreator(Log log) {
            super(log);
        }

        @Override
        ClassVisitor create(ClassVisitor classVisitor) {
            return new ProcessBuilderClassVisitor(classVisitor);
        }
    }

    public static class DexerMainClassAdapterCreator extends ClassAdapterCreator {
        private Map<Method, MethodVisitorCreator> methodAdapterCreatorMap = new HashMap<Method, MethodVisitorCreator>(){
            {
                put(new Method("processClass", "(Ljava/lang/String;[B)Z")
                        , new MethodVisitorCreator.DexerMainMethodVisitorCreator());
            }
        };

        public DexerMainClassAdapterCreator(Log log) {
            super(log);
        }

        @Override
        ClassVisitor create(ClassVisitor classVisitor) {
            return new MarkClassVisitor(classVisitor, methodAdapterCreatorMap, log);
        }
    }
}
