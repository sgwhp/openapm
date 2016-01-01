package com.github.sgwhp.openapm.gradleplugin;

import com.github.sgwhp.openapm.agent.TransformAgent;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;

/**
 * Created by wuhongping on 15-11-10.
 */
public class OpenAPMPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);
        try {
            String jarFilePath = TransformAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            jarFilePath = new File(jarFilePath).getCanonicalPath();
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(jarFilePath, System.getProperty("openapm.agentArgs"));
            vm.detach();
        } catch (URISyntaxException | IOException | AgentInitializationException | AttachNotSupportedException | AgentLoadException e) {
            throw new RuntimeException(e);
        }
    }
}
