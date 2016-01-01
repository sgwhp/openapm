package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by wuhongping on 15-11-19.
 */
public class ProcessBuilderInvocationHandler implements InvocationHandler {
    private InvocationDispatcher dispatcher;
    private Log log;

    public ProcessBuilderInvocationHandler(InvocationDispatcher dispatcher, Log log) {
        this.dispatcher = dispatcher;
        this.log = log;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<String> list = (List<String>) args[0];
        String str1 = list.get(0);
        File file = new File(str1);
        String param = null;
        if (TransformAgent.dx.contains(file.getName().toLowerCase()))
            param = "-Jjavaagent:" + TransformAgent.getAgentPath();
        else if (TransformAgent.java.contains(file.getName().toLowerCase()))
            param = "-javaagent:" + TransformAgent.getAgentPath();
        if (param != null) {
            if (TransformAgent.attachParams != null)
                param = param + "=" + TransformAgent.attachParams;
            list.add(1, toParam(param));
        }
        log.d("Execute: " + list.toString());
        return null;
    }

    private String toParam(String param) {
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            return "\"" + param + "\"";
        return param;
    }
}
