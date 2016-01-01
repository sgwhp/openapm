package com.github.sgwhp.openapm.sample;

import android.app.Application;

import com.github.sgwhp.openapm.monitor.Monitor;

/**
 * Created by wuhongping on 15-12-4.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Monitor.getInstance().start(this);
    }
}
