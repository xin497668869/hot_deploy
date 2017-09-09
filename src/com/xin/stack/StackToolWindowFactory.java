package com.xin.stack;

import com.alibaba.druid.sql.SQLUtils;
import com.intellij.database.model.DasNamespace;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.sql.psi.SqlLanguage;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.Content;
import com.xin.action.start.RunTestAction;
import com.xin.stack.gui.ContentJpanel;
import com.xin.stack.gui.MyLanguageTextField;
import com.xin.stack.gui.SqlJPanel;

import javax.swing.*;
import java.awt.*;

import static com.xin.action.start.RunTestAction.ResponseDataTopic;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class StackToolWindowFactory implements ToolWindowFactory {

    public static final String TAB_CURRENT_FILE = "处理时间";

    @Override
    public void createToolWindowContent(Project project, final ToolWindow toolWindow) {
        addIssuesTab(project, toolWindow);
        toolWindow.setType(ToolWindowType.DOCKED, null);
    }

    public static DasNamespace test;

    private static void addIssuesTab(Project project, ToolWindow toolWindow) {
//        ProjectBindingManager projectBindingManager = SonarLintUtils.get(project, ProjectBindingManager.class);
//        IssueManager issueManager = SonarLintUtils.get(project, IssueManager.class);
//        CurrentFileController scope = new CurrentFileController(project, issueManager);

//        SonarLintIssuesPanel issuesPanel = new SonarLintIssuesPanel(project, projectBindingManager, scope);
        ContentJpanel contentJpanel = new ContentJpanel(project);
        JPanel buttomContent = new JPanel(new BorderLayout());

        JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, contentJpanel, buttomContent);
        Content issuesContent = toolWindow.getContentManager().getFactory()
                .createContent(
                        jSplitPane,
                        TAB_CURRENT_FILE,
                        false);
//        toolWindow.getContentManager().addDataProvider(contentJpanel);
        toolWindow.getContentManager().addContent(issuesContent);


        project.getMessageBus().connect().subscribe(ResponseDataTopic, new RunTestAction.ResponseDataTopicListener() {
            @Override
            public void come(ResponseData responseData) {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        buttomContent.removeAll();

                        JBTabbedPane jbTabbedPane = new JBTabbedPane(SwingConstants.TOP);
                        for (int i = 0; i < responseData.getSql().size(); i++) {
                            MyLanguageTextField languageTextField = new MyLanguageTextField(SqlLanguage.findLanguageByID("MySQL")
                                    , project
                                    , SQLUtils.formatMySql(responseData.getSql().get(i))
                                    , false
                                    , false);
                            JPanel executeAndResultJpanel = new SqlJPanel(project, responseData, languageTextField);
                            executeAndResultJpanel.setName("<" + i + ">");
                            jbTabbedPane.add(executeAndResultJpanel);
                        }

                        buttomContent.add(jbTabbedPane, BorderLayout.NORTH);


                        contentJpanel.replainTreeVo(responseData.getTreeVo());
                    }
                });
            }
        });

    }


    public static String changeToExplain(String sql) {
        return "EXPLAIN " + sql;
    }
}
