package com.xin.action.setting;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.xin.base.BaseAnAction;
import com.xin.base.PluginHelper;
import com.xin.setting.SettingDialog;

import java.io.IOException;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class RunTestSettingAction extends BaseAnAction {

    @Override
    public void actionPerformed(PluginHelper pluginHelper, Project project, Editor editor) throws IOException {
        SettingDialog dialog = new SettingDialog(project);
        dialog.pack();
        dialog.setVisible(true);
    }
}
