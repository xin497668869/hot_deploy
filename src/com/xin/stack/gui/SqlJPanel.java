package com.xin.stack.gui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.panels.VerticalLayout;
import com.xin.stack.ResponseData;

import javax.swing.*;
import java.awt.*;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class SqlJPanel extends JPanel {

    public SqlJPanel(Project project, ResponseData responseData, MyLanguageTextField sqlTextField) {
        setLayout(new VerticalLayout(10));
        add(sqlTextField);

        JPanel buttons = new JPanel(new GridLayout(1, 3));

        JPanel resultJPanel = new JPanel(new VerticalLayout(5));
        JButton explainButton = new ExplainButton(responseData, project, resultJPanel, sqlTextField);

        buttons.add(explainButton);
//        buttons.add(new JButton("execute"));
        JButton positionButton = new PositionButton(resultJPanel, project, responseData);

        buttons.add(positionButton);
        add(buttons);

        add(resultJPanel);
    }

}
