package com.xin.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.xin.base.BaseAnAction;
import com.xin.base.PluginHelper;
import org.apache.commons.lang3.StringUtils;

import static com.xin.action.RunTestAction.requestTest;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 */
public class ReRunTestAction extends BaseAnAction {

    private static final Logger log = Logger.getInstance(ReRunTestAction.class);

    @Override
    public void actionPerformed(PluginHelper pluginHelper, Project project, Editor editor) {
        String lastRequest = PropertiesComponent.getInstance(project).getValue("lastRequest");

        if (StringUtils.isEmpty(lastRequest)) {
            Messages.showInfoMessage("第一次还没有发送", "提示");
            return;
        }

        requestTest(project, lastRequest);
    }
}
