package com.github.sgwhp.openapm.agent.util;

/**
 * Created by wuhongping on 15-11-19.
 */
public interface Log {
    void v(String log);

    void d(String log);

    void w(String log);

    void w(String log, Throwable throwable);

    void e(String log);

    void e(String log, Throwable throwable);
}
