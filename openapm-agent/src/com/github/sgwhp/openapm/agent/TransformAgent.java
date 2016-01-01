package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.FileLog;
import com.github.sgwhp.openapm.agent.util.Log;
import com.github.sgwhp.openapm.agent.util.StreamUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by wuhongping on 15-11-10.
 */
public class TransformAgent {
    public static final Class LOGGER = Logger.class;
    public static final Set<String> dx = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[] { "dx", "dx.bat" })));
    public static final Set<String> java = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[] { "java", "java.exe" })));
    public static final Set<String> skip = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[] {"com/github/sgwhp/openapm/monitor"})));
    public static String attachParams;

    public static void agentmain(String args, Instrumentation inst){
        premain(args, inst);
    }

    public static void premain(String args, Instrumentation inst) {
        attachParams = args;
        Map<String, String> params = Collections.emptyMap();
        boolean error = false;
        try{
            params = parseArguments(args);
        } catch (Exception e){
            error = true;
        }
//        String logFile = params.get("logfile");
//        Log log = logFile == null ? new ConsoleLog() : new FileLog(logFile);
        Log log = new FileLog("log.txt");
        if(error){
            log.e("Arguments parse error: " + args);
        }
        if(params.containsKey("instrumentationDisabled")){
            log.v("instrumentation disabled");
            return;
        }
        try {
            IClassTransformer modifier = new ClassTransformer(log);
            createInvocationDispatcher(log);
            inst.addTransformer(modifier, true);
            Class[] classes = inst.getAllLoadedClasses();
            ArrayList<Class> classesToBeTransform = new ArrayList<>();
            for (Class cls : classes) {
                if(modifier.transforms(cls)){
                    classesToBeTransform.add(cls);
                }
            }
            if(!classesToBeTransform.isEmpty()){
                if(inst.isRetransformClassesSupported()){
                    log.d("retransform classes: " + classesToBeTransform);
                    inst.retransformClasses(classesToBeTransform.toArray(new Class[classesToBeTransform.size()]));
                } else {
                    log.e("unable to transform classes: " + classesToBeTransform);
                }
            }
            redefineClass(inst, modifier, ProcessBuilder.class);
        } catch (Exception e) {
            log.e("agent startup error", e);
            throw new RuntimeException("agent startup error");
        }
    }

    private static void createInvocationDispatcher(Log log) throws Exception {
        Field treeLock = LOGGER.getDeclaredField("treeLock");
        treeLock.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(treeLock, treeLock.getModifiers() & 0xFFFFFFEF);//去掉final
        if (!(treeLock.get(null) instanceof InvocationDispatcher)) {
            treeLock.set(null, new InvocationDispatcher(log));
        }
    }

    private static void redefineClass(Instrumentation instrumentation, ClassFileTransformer transformer, Class<?> klass)
            throws IOException, IllegalClassFormatException, ClassNotFoundException, UnmodifiableClassException {
        String internalName = klass.getName().replace('.', '/');
        String fullName = internalName + ".class";
        ClassLoader classLoader = klass.getClassLoader() == null ? TransformAgent.class.getClassLoader() : klass.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fullName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamUtil.copy(inputStream, outputStream);
        inputStream.close();
        byte[] arrayOfByte = transformer.transform(klass.getClassLoader(), internalName, klass
                , null, outputStream.toByteArray());
        ClassDefinition classDefinition = new ClassDefinition(klass, arrayOfByte);
        instrumentation.redefineClasses(classDefinition);
    }

    public static String genDispatcherKey(String className, String methodName){
        return className + "." + methodName;
    }

    public static String getAgentPath() throws URISyntaxException {
        return new File(TransformAgent.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
    }

    private static Map<String, String> parseArguments(String args) {
        if (args == null)
            return Collections.emptyMap();
        HashMap<String, String> result = new HashMap<>();
        String[] params = args.split(";");
        for (int i = 0, n = params.length; i < n; i++) {
            String str = params[i];
            String[] strs = params[i].split("=");
            if (strs.length != 2)
                throw new IllegalArgumentException("Invalid argument: " + str);
            result.put(strs[0], strs[1]);
        }
        return result;
    }
}
