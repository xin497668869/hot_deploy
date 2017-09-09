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

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.xin.stack.TreeVo;
import org.jetbrains.org.objectweb.asm.Type;

import java.util.Objects;


public class LiveIssue {

    private VirtualFile                          virtualFile;
    private com.intellij.openapi.editor.Document document;
    private TextRange                            textRange;
    private Integer                              length;
    private String                               message;
    private long                                 timeConsume;
    private Project                              project;

    public LiveIssue(TreeVo treeVo, PsiClass psiClass, Project project) {
        this.project = project;
        this.document = PsiDocumentManager.getInstance(project).getDocument(psiClass.getContainingFile());
        PsiMethod psiMethod = getMethod(treeVo, psiClass);
        if (psiMethod != null) {
            this.textRange = psiMethod.getNameIdentifier().getTextRange();
        }
        this.message = treeVo.getTimeConsuming() + "ms | " + treeVo.getMethodName() + "(" + treeVo.getClassName() + ") ";
        this.timeConsume = treeVo.getTimeConsuming();
        this.virtualFile = psiClass.getContainingFile().getVirtualFile();
    }

    private PsiMethod getMethod(TreeVo treeVo, PsiClass psiClass) {
        Type[] argumentTypes = Type.getArgumentTypes(treeVo.getParamClassNames());
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            if (Objects.equals(psiMethod.getName(), treeVo.getMethodName()) && psiMethod.getParameterList().getParameters().length == argumentTypes.length) {
                if (psiMethod.getParameterList().getParameters().length == 0) {
                    return psiMethod;
                }
                if (compareMethod(argumentTypes, psiMethod)) {
                    return psiMethod;
                }
            }
        }
        return null;
    }

    private boolean compareMethod(Type[] argumentTypes, PsiMethod psiMethod) {
        for (int i = 0; i < psiMethod.getParameterList().getParameters().length; i++) {
            PsiType type = psiMethod.getParameterList().getParameters()[i].getType();
            String className;
            if (type instanceof PsiArrayType) {
                className = ((PsiArrayType) type).getComponentType().getCanonicalText() + "[]";
            } else {
                className = type.getCanonicalText();
            }
            if (!className.equals(argumentTypes[i].getClassName())) {
                return false;
            } else {
                System.out.println("出现异常");
            }
        }
        return true;
    }

    public VirtualFile getVirtualFile() {
        return virtualFile;
    }

    public void setVirtualFile(VirtualFile virtualFile) {
        this.virtualFile = virtualFile;
    }

    public TextRange getTextRange() {
        return textRange;
    }

    public void setTextRange(TextRange textRange) {
        this.textRange = textRange;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Project getProject() {
        return project;
    }

    public long getTimeConsume() {
        return timeConsume;
    }
}
