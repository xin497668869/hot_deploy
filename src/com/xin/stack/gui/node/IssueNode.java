/*
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.xin.stack.gui.node;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.SimpleTextAttributes;
import com.xin.stack.gui.tree.TreeCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class IssueNode extends AbstractNode {
    public static final Icon ICON = IconLoader.getIcon("/images/haha.png");
    private final LiveIssue issue;

    public IssueNode(LiveIssue issue) {
        this.issue = issue;
    }

    @Override
    public void render(TreeCellRenderer renderer) {

        renderer.append(issueCoordinates(issue), SimpleTextAttributes.GRAY_ATTRIBUTES);
        renderer.setIcon(ICON);
        renderer.setToolTipText("Double click to open location");
        renderer.append(issue.getMessage());


//    if (issue.getCreationDate() != null) {
//      String creationDate = DateUtils.toAge(issue.getCreationDate());
//      renderer.append(creationDate, SimpleTextAttributes.GRAY_ATTRIBUTES);
//    }
    }

    private void setIcon(TreeCellRenderer renderer, Icon icon) {
        renderer.setIcon(icon);
    }


    @Override
    public int getIssueCount() {
        return 1;
    }

    @Override
    public int getFileCount() {
        return 0;
    }

    public LiveIssue issue() {
        return issue;
    }

    private static String issueCoordinates(@NotNull LiveIssue issue) {
        return "";
    }
}
