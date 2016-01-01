package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.*;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;

/**
 * Created by wuhongping on 15-11-11.
 */
public class ClassTransformer implements IClassTransformer {
    private Log log;
    private final HashMap<String, ClassAdapterCreator> classAdapterFactory = new HashMap<>();

    public ClassTransformer(Log log){
        this.log = log;
        classAdapterFactory.put("java/lang/ProcessBuilder"
                , new ClassAdapterCreator.ProcessBuilderClassAdapterCreator(log));
        classAdapterFactory.put("com/android/dx/command/dexer/Main"
                , new ClassAdapterCreator.DexerMainClassAdapterCreator(log));
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined
            , ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassAdapterCreator creator = classAdapterFactory.get(className);
        if(creator != null){
            try{
                log.d("transforming " + className);
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                ClassVisitor classAdapter = creator.create(cw);
                cr.accept(classAdapter, ClassReader.SKIP_FRAMES);
                return cw.toByteArray();
            } catch (TransformedException e){
            } catch (Exception e){
                log.e("transform class error", e);
            }
        }
        return null;
    }

    @Override
    public boolean transforms(Class<?> klass) {
        return classAdapterFactory.containsKey(Type.getType(klass).getInternalName());
    }
}
