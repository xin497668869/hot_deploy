package com.xin.stack.gui;

import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.xin.stack.ResponseData;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class PositionButton extends JButton {
    private JPanel       parents;
    private Project      project;
    private ResponseData responseData;

    public class Action extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel stackElementJPanel = new JPanel(new VerticalLayout(10));
            parents.removeAll();
            for (StackTraceElement stackTraceElement : responseData.getStackTraceElement()) {
                PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(stackTraceElement.getClassName(), GlobalSearchScope.projectScope(project));
                if (aClass == null) {
                    continue;
                }
                OpenFileHyperlinkInfo openFileHyperlinkInfo = new OpenFileHyperlinkInfo(project, aClass.getContainingFile().getVirtualFile(), stackTraceElement.getLineNumber() - 1);
                stackElementJPanel.add((LinkLabel.create(stackTraceElement.getMethodName(), () -> openFileHyperlinkInfo.navigate(project))));

                System.out.println(aClass);
                openFileHyperlinkInfo.navigate(project);
            }
            parents.add(stackElementJPanel);
        }
    }

    public PositionButton(JPanel parents, Project project, ResponseData responseData) {
        this.parents = parents;
        this.project = project;
        this.responseData = responseData;
        setText("Position");
        addActionListener(new Action());
    }
}
