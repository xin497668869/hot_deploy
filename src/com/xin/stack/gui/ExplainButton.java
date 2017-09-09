package com.xin.stack.gui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.table.JBTable;
import com.xin.sql.anly.ExplainParser;
import com.xin.sql.anly.ExplainResult;
import com.xin.stack.ResponseData;
import com.xin.tools.Mysql;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.xin.stack.StackToolWindowFactory.changeToExplain;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class ExplainButton extends JButton {
    private ResponseData        responseData;
    private Project             project;
    private JPanel              parents;
    private MyLanguageTextField sqllTextField;

    public class Action extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            Mysql.runSql(project, responseData.getDbDetailInfoVo(), statement -> {
                parents.removeAll();

                JLabel tipsLabel = new JLabel("执行中...");
                tipsLabel.setVisible(true);
                tipsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                tipsLabel.setText("执行中");
                parents.add(tipsLabel);
                Map<String, Object> sqlExplain = new HashMap<>();
                try {
                    ResultSet resultSet = statement.executeQuery(changeToExplain(sqllTextField.getText()));
                    Object[][] playerInfo = new Object[resultSet.getMetaData().getColumnCount()][2];
                    if (resultSet.next()) {


                        for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                            playerInfo[i][0] = resultSet.getMetaData().getColumnName(i + 1);
                            playerInfo[i][1] = resultSet.getObject(i + 1);
                            if (playerInfo[i][1] != null) {
                                sqlExplain.put(String.valueOf(playerInfo[i][0]), playerInfo[i][1]);
                            }
                        }
                    }
                    resultSet.close();

                    ApplicationManager.getApplication().invokeLater(() -> {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        String[] columnNames = {"属性", "值"};
                        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, columnNames);
                        tipsLabel.setVisible(false);
                        model.setDataVector(playerInfo, columnNames);
                        JBTable jbTable = new JBTable(model) {
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        parents.add(jbTable);
                    });

                    parents.add(new JTextArea(ExplainParser.parser(convertMap(sqlExplain))));
                } catch (SQLException e1) {
                    Messages.showErrorDialog(project, "sql执行异常: " + e1.getMessage() + "\n" + sqllTextField.getText(), "提示");
                    e1.printStackTrace();
                }

            });

        }
    }

    public ExplainButton(ResponseData responseData, Project project, JPanel parents, MyLanguageTextField sqlTextField) {
        this.responseData = responseData;
        this.project = project;
        this.parents = parents;
        this.sqllTextField = sqlTextField;
        setText("explain");

        addActionListener(new Action());
    }


    public static ExplainResult convertMap(Map<String, Object> map) {
        ExplainResult explainResult = new ExplainResult();
        explainResult.setId(Integer.valueOf(map.getOrDefault("id", 0).toString()));
        explainResult.setSelectType(String.valueOf(map.getOrDefault("selectType", "")));
        explainResult.setTable(String.valueOf(map.getOrDefault("table", "")));
        explainResult.setPartitions(String.valueOf(map.getOrDefault("partitions", "")));
        explainResult.setType(String.valueOf(map.getOrDefault("type", "")));
        explainResult.setPossibleKeys(String.valueOf(map.getOrDefault("possible_keys", "")));
        explainResult.setKey(String.valueOf(map.getOrDefault("key", "")));
        Object keyLen = map.getOrDefault("key_len", 0);
        if (keyLen == null) {
            keyLen = 0;
        }
        explainResult.setKeyLen(Integer.valueOf(keyLen.toString()));
        explainResult.setRef(String.valueOf(map.getOrDefault("ref", "")));
        explainResult.setRows(Integer.valueOf(map.getOrDefault("rows", 0).toString()));
        explainResult.setFiltered(String.valueOf(map.getOrDefault("filtered", "")));
        explainResult.setExtra(String.valueOf(map.getOrDefault("extra", "")));

        return explainResult;
    }


}
