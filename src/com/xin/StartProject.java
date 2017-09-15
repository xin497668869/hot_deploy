package com.xin;

import com.intellij.execution.CommonJavaRunConfigurationParameters;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.messages.MessageBus;
import com.xin.setting.SettingDialog;
import com.xin.setting.SettingProperty;
import com.xin.tools.NetUtils;
import com.zeroturnaround.javarebel.conf.RebelPropertyResolver;
import org.jetbrains.annotations.NotNull;
import org.zeroturnaround.bundled.org.bouncycastle.crypto.signers.RSADigestSigner;

import java.io.File;
import java.io.IOException;
import java.util.WeakHashMap;
import java.util.stream.Collectors;


/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class StartProject implements StartupActivity {

    private static final Logger log = Logger.getInstance(StartProject.class);

    public WeakHashMap<String, String> executorIdParamMap = new WeakHashMap<>();

    static {
        initData();
    }

    public static void initData() {
        String hotDeployLocation = SettingProperty.getProjectLocation();
        //设置jrebel的javaagent包
        System.setProperty("griffin.jar.location", hotDeployLocation + File.separator + "classes" + File.separator + "jrebel6" + File.separator + "jrebel.jar");

        try {
            RebelPropertyResolver.add("rebel.license", hotDeployLocation + File.separator + "classes" + File.separator + "jrebel6" + File.separator + "jrebel_test.lic");
        } catch (IOException e) {
            log.error("rebel.license设置失败", e);
        }
        //提前加载 RSADigestSigner
        RSADigestSigner.class.getName();
    }

    @Override
    public void runActivity(@NotNull Project project) {

        SettingProperty.setProjectDefaultSetting(project);

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

            /**
             * 获取hotdeploy 的javaagent参数
             *
             * @param hotDeployPath
             * @return
             */
            public String getHotDeployParam(File hotDeployPath) {
                if (SettingProperty.getIsHotdeploy(project)) {
                    File hotDeployFile = new File(hotDeployPath, SettingProperty.HOT_DEPLOY_AGENT + File.separator + "hot_deploy_agent.jar");
                    if (hotDeployFile.exists()) {
                        int availablePort = NetUtils.getAvailablePort();
                        SettingProperty.setProjectPort(project, availablePort);
                        String value = SettingProperty.getMonitorClass(project).stream().collect(Collectors.joining(","));
                        return " -javaagent:\"" + SettingProperty.getProjectLocation() + File.separator + SettingProperty.HOT_DEPLOY_AGENT + File.separator + "target" + File.separator + "hot_deploy_agent.jar\"" +
                                "=\"" + availablePort + (value != null ? ";;;" + value : "\"");
                    } else {
                        Messages.showInfoMessage("hot_deploy不存在插件当中,无法使用,  请禁用 " + hotDeployFile.getAbsolutePath(), "提示");
                        SettingDialog dialog = new SettingDialog(project);
                        dialog.pack();
                        dialog.setVisible(true);
                    }
                }
                return "";
            }

            public String getXrebelParam(File hotDeployPath) {
                if (SettingProperty.getIsXrebel(project)) {
                    File xrebelFile = new File(hotDeployPath, SettingProperty.HOT_DEPLOY_AGENT + File.separator + "xrebel.jar");
                    if (xrebelFile.exists()) {
                        return " -javaagent:\"" + xrebelFile.getAbsolutePath() + "\"";
                    } else {
                        Messages.showInfoMessage("xrebel不存在插件当中, 无法使用, 请禁用 " + xrebelFile.getAbsolutePath(), "提示");
                        SettingDialog dialog = new SettingDialog(project);
                        dialog.pack();
                        dialog.setVisible(true);
                    }
                }
                return "";

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
