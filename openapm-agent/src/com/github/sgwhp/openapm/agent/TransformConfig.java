package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;
import com.github.sgwhp.openapm.agent.util.StreamUtil;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by wuhongping on 15-11-23.
 */
public class TransformConfig {
    public static final String EXCEPTION = "EXCEPTION:";
    public static final String TARGET_PACKAGE = "targetPackage";
    private final HashSet<String> targetException;
    private final String targetPackage;

    public TransformConfig(Log log)
            throws ClassNotFoundException {
        Map<String, String> properties = parseProperties(log);
        targetException = parseException(properties, log);
        targetPackage = parseTargetPackage(properties, log);
    }

    public String getTargetPackage(){
        return targetPackage;
    }

    private static String parseTargetPackage(Map<String, String> properties, Log log){
        return properties.get(TARGET_PACKAGE);
    }

    public HashSet<String> getExceptions(){
        return targetException;
    }

    private static HashSet<String> parseException(Map<String, String> properties, Log log){
        HashSet<String> result = new HashSet<>();
        for(String key : properties.keySet()){
            if (key.startsWith(EXCEPTION)) {
                result.add(key.substring(EXCEPTION.length()));
            }
        }
//        properties.entrySet().stream().filter(entry -> entry.getKey().startsWith("")).forEach(
//                entry -> result.add(entry.getKey().substring(EXCEPTION.length())));
        return result;
    }

    private static Map parseProperties(Log log) {
        Properties properties = new Properties();
        URL url = TransformConfig.class.getResource("/config.properties");
        if (url == null) {
            log.e("Unable to find the type map");
            System.exit(1);
        }
        InputStream is = null;
        try {
            is = url.openStream();
            properties.load(is);
        } catch (Exception e) {
            log.e("Unable to read the config file", e);
            System.exit(1);
        } finally {
            StreamUtil.closeInputStreamIgnoreException(is);
        }
        return properties;
    }
}
