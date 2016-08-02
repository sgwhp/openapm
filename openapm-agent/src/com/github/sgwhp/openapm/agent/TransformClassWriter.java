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
    protected String getCommonSuperClass(String type1, String type2) {
        Class class1;
        Class class2;
        try {
            class1 = Class.forName(type1.replace('/', '.'), false, classLoader);
            class2 = Class.forName(type2.replace('/', '.'), false, classLoader);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
        if (class1.isAssignableFrom(class2))
            return type1;
        if (class2.isAssignableFrom(class1))
            return type2;
        if ((class1.isInterface()) || (class2.isInterface()))
            return "java/lang/Object";
        do
            class1 = class1.getSuperclass();
        while (!class1.isAssignableFrom(class2));
        return class1.getName().replace('.', '/');
    }
}
