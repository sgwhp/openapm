package com.github.sgwhp.openapm.agent.util;

/**
 * Created by wuhongping on 15-11-19.
 */
public class ConsoleLog implements Log {
    @Override
    public void v(String log) {
        System.out.println("[openapm.v] " + log);
    }

    @Override
    public void d(String log) {
        System.out.println("[openapm.d] " + log);
    }

    @Override
    public void w(String log) {
        System.err.println("[openapm.w] " + log);
    }

    @Override
    public void w(String log, Throwable throwable) {
        System.err.println("[openapm.w] " + log);
        throwable.printStackTrace(System.err);
    }

    @Override
    public void e(String log) {
        System.err.println("[openapm.e] " + log);
    }

    @Override
    public void e(String log, Throwable throwable) {
        System.err.println("[openapm.e] " + log);
        throwable.printStackTrace(System.err);
    }
}
