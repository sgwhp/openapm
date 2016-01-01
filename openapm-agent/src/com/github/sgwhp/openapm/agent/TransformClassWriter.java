package com.github.sgwhp.openapm.agent;

import org.objectweb.asm.ClassWriter;

/**
 * Created by wuhongping on 15-11-26.
 */
public class TransformClassWriter extends ClassWriter {
    private ClassLoader classLoader;

    public TransformClassWriter(int flags, ClassLoader classLoader) {
        super(flags);
        this.classLoader = classLoader;
    }

    @Override
    protected String getCommonSuperClass(String s, String s1) {
        Class localClass1;
        Class localClass2;
        try {
            localClass1 = Class.forName(s.replace('/', '.'), false, classLoader);
            localClass2 = Class.forName(s1.replace('/', '.'), false, classLoader);
        } catch (Exception localException) {
            throw new RuntimeException(localException.toString());
        }
        if (localClass1.isAssignableFrom(localClass2))
            return s;
        if (localClass2.isAssignableFrom(localClass1))
            return s1;
        if ((localClass1.isInterface()) || (localClass2.isInterface()))
            return "java/lang/Object";
        do
            localClass1 = localClass1.getSuperclass();
        while (!localClass1.isAssignableFrom(localClass2));
        return localClass1.getName().replace('.', '/');
    }
}
