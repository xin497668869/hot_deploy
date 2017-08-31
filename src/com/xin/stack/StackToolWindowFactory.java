package com.xin.stack;

import com.alibaba.druid.sql.SQLUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.sql.psi.SqlLanguage;
import com.intellij.ui.content.Content;
import com.intellij.ui.table.JBTable;
import com.xin.action.start.RunTestAction;
import com.xin.stack.gui.ContentJpanel;
import com.xin.stack.gui.MyLanguageTextField;
import com.xin.tools.Mysql;
import org.jdesktop.swingx.VerticalLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.xin.action.start.RunTestAction.ResponseDataTopic;
import static java.awt.FlowLayout.LEFT;

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

    private static void addIssuesTab(Project project, ToolWindow toolWindow) {
//        ProjectBindingManager projectBindingManager = SonarLintUtils.get(project, ProjectBindingManager.class);
//        IssueManager issueManager = SonarLintUtils.get(project, IssueManager.class);
//        CurrentFileController scope = new CurrentFileController(project, issueManager);
//        SonarLintIssuesPanel issuesPanel = new SonarLintIssuesPanel(project, projectBindingManager, scope);
        ContentJpanel contentJpanel = new ContentJpanel(project);
        JPanel buttomContent = getButtomContent(project);
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, contentJpanel, buttomContent);

        Content issuesContent = toolWindow.getContentManager().getFactory()
                .createContent(
                        jSplitPane,
                        TAB_CURRENT_FILE,
                        false);
//        toolWindow.getContentManager().addDataProvider(contentJpanel);
        toolWindow.getContentManager().addContent(issuesContent);
    }

    @NotNull
    private static JPanel getButtomContent(Project project) {
        JPanel buttomContent = new JPanel(new BorderLayout());

        project.getMessageBus().connect().subscribe(ResponseDataTopic, new RunTestAction.ResponseDataTopicListener() {
            @Override
            public void come(ResponseData responseData) {
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        buttomContent.removeAll();

                        MyLanguageTextField languageTextField = new MyLanguageTextField(SqlLanguage.findLanguageByID("MySQL"), project, SQLUtils.formatMySql("SELECT * FROM SQLASTOutputVisitor WHERE id = 'asdf' LIMIT 1"), false, false);

                        JPanel buttons = new JPanel(new FlowLayout(LEFT));
                        for (int i = 0; i < responseData.getSql().size(); i++) {
                            JButton jButton = new JButton(String.valueOf(i + 1));
                            jButton.setName(String.valueOf(i));
                            jButton.addActionListener(new AbstractAction() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    switchSql(responseData.getSql().get(Integer.parseInt(jButton.getName())), languageTextField);
                                }
                            });
                            buttons.add(jButton);
                        }
                        if (responseData.getSql().size() > 0) {
                            switchSql(responseData.getSql().get(0), languageTextField);
                        }
                        buttomContent.add(buttons, BorderLayout.NORTH);
                        JPanel executeAndResult = getExecuteAndResultJpanel(responseData.getDbDetailInfoVo(), languageTextField);
                        buttomContent.add(executeAndResult, BorderLayout.CENTER);

                    }
                });
            }
        });

        return buttomContent;
    }

    public static void switchSql(String sql, MyLanguageTextField languageTextField) {
        languageTextField.setText(SQLUtils.formatMySql(sql));
    }

    @NotNull
    private static JPanel getExecuteAndResultJpanel(ResponseData.DbDetailInfoVo dbDetailInfoVo, MyLanguageTextField languageTextField) {
        JPanel executeAndResult = new JPanel(new VerticalLayout(10));
        executeAndResult.add(languageTextField);
        JPanel buttons = new JPanel(new GridLayout(1, 3));
        JButton testButton = new JButton("test");
        String[] columnNames = {"属性", "值"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, columnNames);
        testButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Mysql.runSql(dbDetailInfoVo, statement -> {
                    try {
                        ResultSet resultSet = statement.executeQuery(languageTextField.getText());
                        if (resultSet.next()) {
                            Object[][] playerInfo = new Object[2][resultSet.getMetaData().getColumnCount()];
                            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                                playerInfo[0][i] = resultSet.getMetaData().getCatalogName(i + 1);
                                playerInfo[1][i] = resultSet.getString(i + 1);
                            }
                            model.setDataVector(playerInfo, columnNames);
                        }

                        model.fireTableDataChanged();
                        resultSet.close();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                });
            }
        });
        buttons.add(testButton);
        buttons.add(new JButton("execute"));
        buttons.add(new JButton("position"));
        executeAndResult.add(buttons);


        JBTable jbTable = new JBTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };


        executeAndResult.add(jbTable);
        return executeAndResult;
    }

}
