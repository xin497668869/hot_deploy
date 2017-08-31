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
package com.xin.stack.gui.tree;

import com.intellij.ui.ColoredTreeCellRenderer;
import com.xin.stack.gui.node.AbstractNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Can't unit test this because the parent uses a service, depending on a pico container with a method
 * that doesn't exist in the pico container used by SonarLint (different versions), causing NoSuchMethodError.
 */
public class TreeCellRenderer extends ColoredTreeCellRenderer {
    private String iconToolTip = null;

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        AbstractNode node = (AbstractNode) value;
        node.render(this);
    }

    public void setIconToolTip(String toolTip) {
        this.iconToolTip = toolTip;
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (iconToolTip == null) {
            return super.getToolTipText(event);
        }

        if (event.getX() < getIconWidth()) {
            return iconToolTip;
        }

        return super.getToolTipText(event);
    }

    private int getIconWidth() {
        if (getIcon() != null) {
            return getIcon().getIconWidth() + myIconTextGap;
        }
        return 0;
    }
}
