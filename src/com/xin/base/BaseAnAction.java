package com.xin.base;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.io.IOException;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 */
public abstract class BaseAnAction extends AnAction {

    public PluginHelper pluginHelper;

    public abstract void actionPerformed(PluginHelper pluginHelper, Project project, Editor editor) throws IOException;

    @Override
    public void actionPerformed(AnActionEvent event) {
        PluginHelper pluginHelper = new PluginHelper(event);
        Editor editor = pluginHelper.getContext().getEditor();
        Project project = event.getProject();

        this.pluginHelper = pluginHelper;

        try {
            actionPerformed(pluginHelper, project, editor);
        } catch (BaseException e) {
            HintManager.getInstance().showErrorHint(editor, e.getMessage());
            if (e.getInnerException() != null)
                e.getInnerException().printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
