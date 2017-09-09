package com.xin.stack.gui;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.tools.SimpleActionGroup;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.EditSourceOnDoubleClickHandler;
import com.intellij.util.ui.tree.TreeUtil;
import com.xin.stack.TreeVo;
import com.xin.stack.gui.node.AbstractNode;
import com.xin.stack.gui.node.IssueNode;
import com.xin.stack.gui.node.LiveIssue;
import com.xin.stack.gui.node.SummaryNode;
import com.xin.stack.gui.tree.IssueTree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class ContentJpanel extends SimpleToolWindowPanel {


    private Tree             tree;
    private Project          project;
    private ActionToolbar    mainToolbar;
    private SummaryNode      summaryNode;
    private DefaultTreeModel defaultTreeModel;

    public void replainTreeVo(TreeVo treeVo) {
        DumbService.getInstance(project).smartInvokeLater(() -> {
            summaryNode.removeAllChildren();
            initTree(project, treeVo, summaryNode);
            defaultTreeModel.nodeStructureChanged(summaryNode);
            TreeUtil.expandAll(tree);
        });
    }

    public ContentJpanel(Project project) {
        super(false, true);
        this.project = project;
        this.summaryNode = new SummaryNode();
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(summaryNode);
        tree = new IssueTree(project, defaultTreeModel);
        tree.setToggleClickCount(0);
        this.defaultTreeModel = defaultTreeModel;

        JPanel issuesPanel = new JPanel(new BorderLayout());
        TreeUtil.expandAll(tree);
        issuesPanel.add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);

        setToolbar(createActionGroup(actions()));
        super.add(issuesPanel);
        EditSourceOnDoubleClickHandler.install(tree);

    }

    public void initTree(Project project, TreeVo testTreeVo, AbstractNode issueTree) {
        String trueClassName;
        if (testTreeVo.getClassName().contains("$$")) {
            trueClassName = testTreeVo.getClassName().substring(0, testTreeVo.getClassName().indexOf("$$"));
        } else {
            trueClassName = testTreeVo.getClassName();
        }

        PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(trueClassName, GlobalSearchScope.allScope(project));
        if (aClass != null) {

            if (testTreeVo.getChildrens() != null) {

                for (TreeVo treeVo : testTreeVo.getChildrens()) {
                    IssueNode newChild = new IssueNode(new LiveIssue(treeVo, aClass, project));
                    issueTree.add(newChild);
                    initTree(project, treeVo, newChild);
                }
            }
        }
    }


    private static ActionGroup createActionGroup(Collection<AnAction> actions) {
        SimpleActionGroup actionGroup = new SimpleActionGroup();
        actions.forEach(actionGroup::add);
        return actionGroup;
    }


    private static Collection<AnAction> actions() {
        List<AnAction> list = new ArrayList<>();
//        list.add(ActionManager.getInstance().getAction("ScanCodeReviewAnAction"));
        return list;
    }

    protected void setToolbar(ActionGroup group) {
        if (mainToolbar != null) {
            mainToolbar.setTargetComponent(null);
            super.setToolbar(null);
            mainToolbar = null;
        }
        mainToolbar = ActionManager.getInstance().createActionToolbar("code review", group, false);
        mainToolbar.setTargetComponent(this);
        Box toolBarBox = Box.createHorizontalBox();
        toolBarBox.add(mainToolbar.getComponent());

        super.setToolbar(toolBarBox);
        mainToolbar.getComponent().setVisible(true);
    }

}
