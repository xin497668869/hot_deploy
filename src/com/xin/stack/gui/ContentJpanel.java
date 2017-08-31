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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class ContentJpanel extends SimpleToolWindowPanel {


    private Tree          tree;
    private Project       project;
    private ActionToolbar mainToolbar;
    private SummaryNode   summaryNode;

    public ContentJpanel(Project project) {
        super(false, true);
        this.project = project;
        this.summaryNode = new SummaryNode();
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(summaryNode);
        tree = new IssueTree(project, defaultTreeModel);
        tree.setToggleClickCount(0);
        DumbService.getInstance(project).smartInvokeLater(() -> {
            TreeVo testTreeVo = getTestTreeVo();
            initTree(project, testTreeVo, summaryNode);
            defaultTreeModel.nodeStructureChanged(summaryNode);
        });


        JPanel issuesPanel = new JPanel(new BorderLayout());
        TreeUtil.expandAll(tree);
        issuesPanel.add(ScrollPaneFactory.createScrollPane(tree), BorderLayout.CENTER);

        setToolbar(createActionGroup(actions()));
        super.add(issuesPanel);
        EditSourceOnDoubleClickHandler.install(tree);

    }

    public void initTree(Project project, TreeVo testTreeVo, AbstractNode issueTree) {
        PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(testTreeVo.getClassName(), GlobalSearchScope.allScope(project));
        if (aClass != null) {

            IssueNode newChild = new IssueNode(new LiveIssue(testTreeVo, aClass, project));
            issueTree.add(newChild);
            if (testTreeVo.getTreeVo() != null) {
                for (TreeVo treeVo : testTreeVo.getTreeVo()) {
                    initTree(project, treeVo, newChild);
                }
            }
        }
    }

    public TreeVo getTestTreeVo() {
        TreeVo treeVo = new TreeVo("SQLASTOutputVisitor.Main2", "main", Arrays.asList(String[].class.getName()), 3000L);
        TreeVo treeVo1 = new TreeVo("SQLASTOutputVisitor.Test1", "test1", Arrays.asList(String.class.getName()), 1000L);
        TreeVo treeVo11 = new TreeVo("SQLASTOutputVisitor.Test1", "test11", Arrays.asList(Boolean.class.getName()), 1000L);
        TreeVo treeVo2 = new TreeVo("SQLASTOutputVisitor.Test2", "test2", Arrays.asList(int.class.getName()), 500L);
        TreeVo treeVo_2 = new TreeVo("SQLASTOutputVisitor.Test2", "test2", Arrays.asList(int.class.getName()), 500L);
        treeVo.setTreeVo(Arrays.asList(treeVo1, treeVo11));
        treeVo1.setTreeVo(Arrays.asList(treeVo2));
        treeVo11.setTreeVo(Arrays.asList(treeVo_2));
        return treeVo;
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
