package com.github.sgwhp.openapm.monitor;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wuhongping on 15-12-3.
 */
public class Monitor {
    private static volatile Monitor instance;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private volatile boolean taskRunning;
    // >0表示写文件的线程数，<0表示文件正在被关闭
    private AtomicInteger writeCount = new AtomicInteger();
    private volatile FileChannel mFileChannel;
    private String logPath;
    private volatile int writeIndex = 1;
    private volatile int readIndex = -writeIndex;
    private boolean debug;

    private Monitor(){ }

    public static Monitor getInstance(){
        if(instance == null){
            synchronized (Monitor.class){
                if(instance == null){
                    instance = new Monitor();
                }
            }
        }
        return instance;
    }

    public void start(Context context){
        CrashHandler.getInstance().init();
        String dirStr = context.getCacheDir().getAbsolutePath() + "/log";
        File dir = new File(dirStr);
        if(!dir.exists()){
            if(!dir.mkdirs()){
                Log.w("openapm", "cannot create directory: " + dirStr);
                return;
            }
        }
        logPath = dirStr + "/exceptions%d.log";
        if(!debug) executor.execute(new UploadExceptionTask());
    }

    public void start(Context context, boolean debug){
        this.debug = debug;
        start(context);
    }

    private void switchIndex(){
        writeIndex = -writeIndex;
        readIndex = - readIndex;
    }

    private String getWriteFilePath(){
        return String.format(logPath, writeIndex);
    }

    private String getReadFilePath(){
        return String.format(logPath, readIndex);
    }

    private FileChannel getFileChannel(){
        if(mFileChannel == null){
            synchronized (Monitor.class){
                if(mFileChannel == null){
                    while(writeCount.get() <0){}
                    try {
                        mFileChannel = new FileOutputStream(getWriteFilePath()).getChannel();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mFileChannel;
    }

    private void resetFileChannel(){
        if(mFileChannel != null){
            try {
                mFileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFileChannel = null;
        }
    }

    /**
     * 写入日志，稍后将自动上传到服务器中
     * @param log 日志内容
     */
    private void pushException(String log) {
        if(debug) return;
        FileChannel fc = null;
        try {
            while (writeCount.get() < 0) {}
            writeCount.incrementAndGet();
            fc = getFileChannel();
            if(fc == null) return;
            fc.write(ByteBuffer.wrap(log.getBytes()));

            if (!taskRunning) {
                executor.execute(new UploadExceptionTask());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeCount.decrementAndGet();
            if (fc != null && writeCount.compareAndSet(0, -1)) {
                //已经没有线程对该文件读写，将其关闭
                try {
                    fc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writeCount.set(0);
            }
        }
    }

    private void pushExceptionInternal(Throwable th){
        if(debug){
            th.printStackTrace();
            return;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.append(Utils.formatDate()).append(":\n");
        th.printStackTrace(pw);
        pw.append("\n");
        pw.flush();
        pushException(sw.toString());
    }

    public static void pushException(Throwable th){
        getInstance().pushExceptionInternal(th);
    }

    private boolean uploadException(String exception){
        // replace with your code here
        System.out.println(exception);
        return true;
    }

    /**
     * 日志上传
     */
    private class UploadExceptionTask implements Runnable{
        private int retry;

        @Override
        public void run() {
            taskRunning = true;
            while(true){
                File file = new File(getReadFilePath());
                String log = Utils.fileToString(file);
                if(log != null){
                    while(retry < 3 && !uploadException(log)) retry++;
                    if(retry < 3) file.delete();
                    else break;
                }
                while(!writeCount.compareAndSet(0, -1)){}
                switchIndex();
                resetFileChannel();
                writeCount.set(0);
                if(!new File(getReadFilePath()).exists()){
                    break;
                }
            }
            taskRunning = false;
        }
    }

}
