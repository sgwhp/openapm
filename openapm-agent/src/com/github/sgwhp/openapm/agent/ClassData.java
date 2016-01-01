package com.github.sgwhp.openapm.agent;

/**
 * Created by wuhongping on 15-11-23.
 */
public class ClassData {
    private final byte[] mainData;
    private final boolean modify;

    public ClassData(byte[] classByte, boolean modify) {
        mainData = classByte;
        this.modify = modify;
    }

    public byte[] getMainClassBytes() {
        return this.mainData;
    }

    public boolean isModified() {
        return this.modify;
    }
}
