package com.xin.base;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.ProjectViewImpl;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.PsiJavaFileImpl;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 */
public class Context {

    private Project       project;
    private PsiElement    psiElement;
    private PsiFile       psiFile;
    private Editor        editor;
    private AnActionEvent anActionEvent;


    public PsiElement[] getSelectedPSIElements() {
        ProjectView instance = ProjectViewImpl.getInstance(project);
        PsiElement[] selectedPSIElements = instance.getCurrentProjectViewPane().getSelectedPSIElements();
        return selectedPSIElements;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }

//    public PsiClass getPsiClass() {
//        PsiElement tem = psiElement;
//        while (tem != null) {
//            if (tem instanceof PsiClass)
//                return (PsiClass) tem;
//            else {
//                tem = tem.getParent();
//            }
//        }
//        return null;
//    }
//
//    public PsiMethod getPsiMethod() {
//        PsiElement tem = psiElement;
//        while (tem != null) {
//            if (tem instanceof PsiMethod)
//                return (PsiMethod) tem;
//            else {
//                tem = tem.getParent();
//            }
//        }
//        return null;
//    }

    public AnActionEvent getAnActionEvent() {
        return anActionEvent;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
        this.project = editor.getProject();
        this.psiFile = ((EditorImpl) editor).getDataContext().getData(PSI_FILE);

    }

    public void setAnActionEvent(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;
        this.project = anActionEvent.getProject();
        this.psiElement = anActionEvent.getData(PSI_ELEMENT);
        this.psiFile = anActionEvent.getData(PSI_FILE);
        this.editor = anActionEvent.getData(EDITOR);
    }

    public PsiFile getPsiFile() {
        return psiFile;
    }

    public PsiClass getMainClass() {
        if (psiFile instanceof PsiJavaFileImpl) {

            PsiClass[] classes = ((PsiJavaFileImpl) psiFile).getClasses();
            String className = getPsiFile().getName().substring(0, getPsiFile().getName().lastIndexOf("."));
            for (PsiClass aClass : classes) {
                if (aClass.getName().equals(className))
                    return aClass;
            }
        }
        return null;
    }

    /**
     * 根据当前位置获取当前主类的方法
     *
     * @return
     */
    public PsiMethod getPositionMethod() {
        int offset = getEditor().getCaretModel().getOffset();
        PsiClass psiClass = getMainClass();
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            if (psiMethod.getTextRange().containsOffset(offset)) {
                return psiMethod;
            }
        }
        return null;
    }

    /**
     * 根据位置获取当前主类的方法
     *
     * @param offset
     * @return
     */
    public PsiMethod getPositionMethod(int offset) {
        PsiClass psiClass = getMainClass();
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            if (psiMethod.getTextRange().containsOffset(offset)) {
                return psiMethod;
            }
        }
        return null;
    }

    public Editor getEditor() {
        return editor;
    }
}
