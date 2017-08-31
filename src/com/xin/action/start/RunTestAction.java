package com.xin.action.start;

import com.alibaba.fastjson.JSON;
import com.intellij.compiler.actions.CompileDirtyAction;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.util.messages.Topic;
import com.xin.base.BaseAnAction;
import com.xin.base.PluginHelper;
import com.xin.stack.ResponseData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

import static com.xin.StartProject.PROJECT_PORT;


/**
 * @author linxixin@cvte.com
 * @version 1.0
 */
public class RunTestAction extends BaseAnAction {
    private static final Logger log = Logger.getInstance(RunTestAction.class);

    public interface ResponseDataTopicListener {
        void come(ResponseData responseData);
    }

    public static Topic<ResponseDataTopicListener> ResponseDataTopic = Topic.create("ResponseDataTopicListener", ResponseDataTopicListener.class);

    @Override
    public void actionPerformed(PluginHelper pluginHelper, Project project, Editor editor) {
        log.info("启动请求了");
//        ModuleRootManager instance = ModuleRootManager.getInstance(ModuleManager.getInstance(project).getModules());
        // ModuleRootManager.getInstance(ModuleManager.getInstance(project).getModules()[0]).orderEntries().classes().getRoots()
        //ModuleManager.getInstance(project).getModules()
        int position = ((EditorImpl) pluginHelper.getContext().getEditor()).getCaretModel().getOffset();

        PsiClass psiClass = pluginHelper.getContext().getMainClass();
        ApplicationManager.getApplication().getComponent(CompileDirtyAction.class);
        PsiMethod positionMethod = pluginHelper.getContext().getPositionMethod(position);

        if (positionMethod == null) {
            Messages.showInfoMessage("请在方法内执行", "提示");
            return;
        }
        //执行以下make project 防止有变动, 可以执行下make, 暂时先不弄了, 自己make
        //CompilerManager.getInstance(project).make(null);
        if (positionMethod.getParameterList().getParametersCount() > 0) {
            Messages.showInfoMessage("测试方法不能有参数哦", "提示");
            return;
        }

        String methodName = positionMethod.getName();
        String className = psiClass.getName();


        if (!(psiClass.getParent() instanceof PsiJavaFileImpl)) {
            Messages.showInfoMessage("java文件才行哦", "提示");
            return;
        }

        String packageName = ((PsiJavaFileImpl) psiClass.getParent()).getPackageName();
        if (!packageName.equals("")) {
            packageName = packageName + ".";
        }
        String requestContent = packageName + className + ":::" + methodName;

        requestTest(project, requestContent);

    }

    public static void requestTest(Project project, String requestContent) {
        int port = PropertiesComponent.getInstance(project).getInt(PROJECT_PORT, 0);
        if (port < 1) {
            log.error("端口没开");
            ApplicationManager.getApplication()
                    .invokeLater(() -> Messages.showInfoMessage("端口没开, 项目启动完了吗 ", "提示"));
        }

        //端口没开
        ApplicationManager.getApplication().invokeLater(new Thread(() -> {
            log.info("准备了请求了 端口 " + port);
            try {
                Socket socket = new Socket("localhost", port);
//                socket.setSoTimeout(3000);
                BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                outputStream.write(requestContent + "\r\n");
                outputStream.flush();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("正在读");
                String response = bufferedReader.readLine();
                System.out.println("读完啦");
                ResponseData responseData = JSON.parseObject(response, ResponseData.class);
                System.out.println(responseData);
                ResponseDataTopicListener responseDataTopicListener = project.getMessageBus().syncPublisher(ResponseDataTopic);
                responseDataTopicListener.come(responseData);
                socket.close();
                PropertiesComponent.getInstance(project).setValue("lastRequest", requestContent);
            } catch (ConnectException e) {
                log.error("端口没开");
                ApplicationManager.getApplication()
                        .invokeLater(() -> Messages.showInfoMessage("端口没开 " + port + ", 项目启动完了吗 " + e.getMessage(), "提示"));
                //端口没开
            } catch (Exception e) {
                log.error(e);
                ApplicationManager.getApplication()
                        .invokeLater(() -> Messages.showInfoMessage("请求异常 " + e.getMessage(), "提示"));
            }

        })::start);
    }


}
