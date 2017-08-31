package com.xin;

import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.support.logging.LogFactory;
import com.intellij.execution.CommonJavaRunConfigurationParameters;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.messages.MessageBus;
import com.xin.gui.SettingDialog;
import com.xin.tools.NetUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.WeakHashMap;

import static com.xin.gui.SettingDialog.IS_HOTDEPLOY;
import static com.xin.gui.SettingDialog.IS_XREBEL;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class StartProject implements StartupActivity {
    public static LogFactory          logFactory;
    public static MySqlOutputVisitor  mySqlOutputVisitor;
    public static SQLASTOutputVisitor sqlastOutputVisitor;

    public static final String                      PROJECT_PORT       = "project_port";
    public              WeakHashMap<String, String> executorIdParamMap = new WeakHashMap<>();

    @Override
    public void runActivity(@NotNull Project project) {

        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        if (!propertiesComponent.isValueSet(IS_HOTDEPLOY)) {
            propertiesComponent.setValue(IS_HOTDEPLOY, false);
        }

        if (!propertiesComponent.isValueSet(IS_XREBEL)) {
            propertiesComponent.setValue(IS_XREBEL, false);
        }

        MessageBus messageBus = project.getMessageBus();
        messageBus.connect().subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {

            @Override
            public void processStartScheduled(@NotNull String executorId, @NotNull ExecutionEnvironment env) {

                RunProfile selectedConfiguration1 = env.getRunProfile();
                if (selectedConfiguration1 instanceof CommonJavaRunConfigurationParameters) {
                    CommonJavaRunConfigurationParameters selectedConfiguration = (CommonJavaRunConfigurationParameters) selectedConfiguration1;
                    File hotDeployPath = PluginManager.getPlugin(PluginId.getId("hot_deploy")).getPath();

                    String orgVmParameters = selectedConfiguration.getVMParameters();
                    if (orgVmParameters == null) {
                        orgVmParameters = "";
                    }
                    executorIdParamMap.put(executorId, orgVmParameters);
                    String hotDeployParam = getHotDeployParam(hotDeployPath);
                    String xrebelParam = getXrebelParam(hotDeployPath);
                    System.out.println("自定义vm :\n热部署: " + hotDeployParam + "\nxrebel:" + xrebelParam);
                    selectedConfiguration.setVMParameters(orgVmParameters + " " + hotDeployParam + " " + xrebelParam);

                }
            }

            public String getHotDeployParam(File hotDeployPath) {
                if (propertiesComponent.getBoolean(IS_HOTDEPLOY)) {
                    File hotDeployFile = new File(hotDeployPath, "hot_deploy_agent" + File.separator + "hot_deploy_agent.jar");
                    if (hotDeployFile.exists()) {
                        int availablePort = NetUtils.getAvailablePort();
                        propertiesComponent.setValue(PROJECT_PORT, String.valueOf(availablePort));
                        return " -javaagent:\"" + hotDeployFile.getAbsolutePath() + "\"=" + availablePort;
                    } else {
                        Messages.showInfoMessage("hot_deploy不存在插件当中,无法使用,  请禁用 " + hotDeployFile.getAbsolutePath(), "提示");
                        SettingDialog dialog = new SettingDialog(project);
                        dialog.pack();
                        dialog.setVisible(true);
                        return "";
                    }
                } else {
                    return "";
                }
            }

            public String getXrebelParam(File hotDeployPath) {
                if (propertiesComponent.getBoolean(IS_XREBEL)) {
                    File xrebelFile = new File(hotDeployPath, "hot_deploy_agent" + File.separator + "xrebel.jar");
                    if (xrebelFile.exists()) {
                        return " -javaagent:\"" + xrebelFile.getAbsolutePath() + "\"";
                    } else {
                        Messages.showInfoMessage("xrebel不存在插件当中, 无法使用, 请禁用 " + xrebelFile.getAbsolutePath(), "提示");
                        SettingDialog dialog = new SettingDialog(project);
                        dialog.pack();
                        dialog.setVisible(true);
                        return "";
                    }
                } else {
                    return "";
                }
            }

            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
                rollback(executorId, env);
            }

            private void rollback(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
                RunProfile selectedConfiguration1 = env.getRunProfile();
                if (selectedConfiguration1 instanceof CommonJavaRunConfigurationParameters) {
                    CommonJavaRunConfigurationParameters selectedConfiguration = (CommonJavaRunConfigurationParameters) selectedConfiguration1;
                    if (executorIdParamMap.containsKey(executorId)) {
                        selectedConfiguration.setVMParameters(executorIdParamMap.get(executorId));
                        executorIdParamMap.clear();
                    }
                }
            }

            @Override
            public void processNotStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
                rollback(executorId, env);
            }
        });

    }
}
