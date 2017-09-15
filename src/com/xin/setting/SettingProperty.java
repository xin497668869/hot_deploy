package com.xin.setting;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author linxixin@cvte.com
 */
public class SettingProperty {


    public static final String IS_HOTDEPLOY   = "is_hotdeploy";
    public static final String IS_XREBEL      = "is_xrebel";
    public static final String MONITOR_CLASS  = "monitor_class";
    public static final String METHOD_TIMEOUT = "method_timeout";

    public static final String PROJECT_PORT     = "project_port";
    public static final String HOT_DEPLOY_AGENT = "hot_deploy_agent";

    /**
     * 设置默认的设置
     *
     * @param project
     * @return
     */
    @NotNull
    public static void setProjectDefaultSetting(@NotNull Project project) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        if (!propertiesComponent.isValueSet(IS_HOTDEPLOY)) {
            propertiesComponent.setValue(IS_HOTDEPLOY, false);
        }

        if (!propertiesComponent.isValueSet(IS_XREBEL)) {
            propertiesComponent.setValue(IS_XREBEL, false);
        }
    }

    public static String getProjectLocation() {
        return PluginManager.getPlugin(PluginId.getId("hot_deploy")).getPath().getAbsolutePath();
    }

    public static boolean getIsHotdeploy(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(IS_HOTDEPLOY, false);
    }

    public static boolean getIsXrebel(Project project) {
        return PropertiesComponent.getInstance(project).getBoolean(IS_XREBEL, false);
    }

    public static List<String> getMonitorClass(Project project) {
        return Arrays.asList(PropertiesComponent.getInstance(project).getValue(MONITOR_CLASS).split("\n"));
    }

    public static long getMethodTimeout(Project project) {
        long timeoutValue = PropertiesComponent.getInstance(project).getInt(METHOD_TIMEOUT, -1);
        if (timeoutValue < 0) {
            timeoutValue = Integer.MAX_VALUE;
        }
        return timeoutValue;
    }

    public static void setProjectPort(Project project, Integer port) {
        PropertiesComponent.getInstance(project).setValue(PROJECT_PORT, port, -1);
    }

}
