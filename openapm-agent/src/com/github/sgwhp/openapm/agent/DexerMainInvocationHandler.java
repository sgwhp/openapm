package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wuhongping on 15-11-23.
 */
public class DexerMainInvocationHandler implements InvocationHandler {
    private InvocationDispatcher dispatcher;
    private Log log;

    public DexerMainInvocationHandler(InvocationDispatcher dispatcher, Log log){
        this.dispatcher = dispatcher;
        this.log = log;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        byte[] classBytes = (byte[]) args[1];
        synchronized (dispatcher.getContext()) {
            ClassData data = dispatcher.transform(classBytes);
            if ((data != null) && (data.getMainClassBytes() != null) && (data.isModified()))
                return data.getMainClassBytes();
        }
        return classBytes;
    }
}
