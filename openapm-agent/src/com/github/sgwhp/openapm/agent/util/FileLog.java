package com.github.sgwhp.openapm.agent.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by wuhongping on 15-11-19.
 */
public class FileLog implements Log {
    private final PrintWriter writer;

    public FileLog(String path){
        try {
            writer = new PrintWriter(new FileOutputStream(path, true));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeLog(String tag, String log){
        writer.write("[" + tag + "] " + log + "\n");
        writer.flush();
    }

    private void writeLog(String tag, String log, Throwable throwable){
        writer.write("[" + tag + "] " + log + "\n");
        throwable.printStackTrace(writer);
        writer.write("\n");
        writer.flush();
    }

    @Override
    public void v(String log) {
        writeLog("openamp.v", log);
    }

    @Override
    public void d(String log) {
        writeLog("openamp.d", log);
    }

    @Override
    public void w(String log) {
        writeLog("openamp.w", log);
    }

    @Override
    public void w(String log, Throwable throwable) {
        writeLog("openamp.w", log, throwable);
    }

    @Override
    public void e(String log) {
        writeLog("openamp.e", log);
    }

    @Override
    public void e(String log, Throwable throwable) {
        writeLog("openamp.e", log, throwable);
    }
}
