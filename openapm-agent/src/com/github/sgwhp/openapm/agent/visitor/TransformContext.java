package com.github.sgwhp.openapm.agent.visitor;

import com.github.sgwhp.openapm.agent.*;
import com.github.sgwhp.openapm.agent.util.Log;

import java.util.*;

/**
 * Created by wuhongping on 15-11-23.
 */
public class TransformContext {
    private final TransformConfig config;
    private final Log log;
    private boolean modified;
    private String className;
    private String superClassName;

    public TransformContext(TransformConfig config, Log log) {
        this.config = config;
        this.log = log;
    }

    public Log getLog() {
        return this.log;
    }

    public void reset() {
        this.modified = false;
        this.className = null;
        this.superClassName = null;
    }

    public void markModified() {
        modified = true;
    }

    public boolean isClassModified() {
        return modified;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public String getFriendlyClassName() {
        return className.replaceAll("/", ".");
    }

    public String getFriendlySuperClassName() {
        return superClassName.replaceAll("/", ".");
    }

    public String getSimpleClassName() {
        if (className.contains("/"))
            return className.substring(className.lastIndexOf("/") + 1);
        return className;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public ClassData newClassData(byte[] data) {
        return new ClassData(data, isClassModified());
    }

    public HashSet<String> getExceptions(){
        return config.getExceptions();
    }

    public String getTargetPackage(){
        return config.getTargetPackage();
    }
}
