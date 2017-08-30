package com.xin.base;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.intellij.psi.PsiModifier.PRIVATE;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 */
public class PluginHelper {

    private Context context = new Context();
    SearchScope searchScope;

    public Context getContext() {
        return context;
    }

    public PluginHelper(Project project) {
        context.setProject(project);
        searchScope = new SearchScope(project);
    }

    public PluginHelper(AnActionEvent anActionEvent) {
        this(anActionEvent.getProject());
        context.setAnActionEvent(anActionEvent);
        PsiClass firstFileClass = findFirstFileClass();
    }

    public PluginHelper(Editor editor) {
        context.setEditor(editor);
        PsiClass firstFileClass = findFirstFileClass();
    }

    public Project getProject() {
        return context.getProject();
    }

    public PsiElementFactory getPsiElementFactory() {
        return PsiElementFactory.SERVICE.getInstance(getProject());
    }

    public PsiClassType getPsiType(Class classType) {
        return PsiType.getTypeByName(classType.getName(), getProject(), searchScope.getAllScope());
    }

    public PsiField createField(String name, Class classType) {

        //PsiClass[] classesByName = PsiShortNamesCache.getInstance(getProject()).getClassesByName(className.getName(), searchScope.getAllScope());
        PsiClass[] classes = JavaPsiFacade.getInstance(getProject()).findClasses(classType.getName(), searchScope.getAllScope());
        if (classes == null || classes.length < 1) {
            throw new BaseException("找不到这个类哦, 奇怪了");
        }

        PsiClass psiClass = classes[0];
        PsiClassType psiClassType = getPsiType(classType);
        PsiImportStatement importStatement = getPsiElementFactory().createImportStatement(psiClass);

        if (context.getPsiFile() instanceof PsiJavaFile) {
            PsiImportList importList = ((PsiJavaFile) context.getPsiFile()).getImportList();
            //用于判断是否存在这个import
            PsiImportStatement existImport = importList.findSingleClassImportStatement(importStatement.getQualifiedName());
            if (existImport != null) {
                importList.add(importStatement);
            }
            PsiField newField = getPsiElementFactory().createField(name, psiClassType);
            newField.getModifierList().setModifierProperty(PRIVATE, true);
            return newField;

        } else {
            throw new BaseException("这个文件不是java文件?");
        }
    }

    //根据virtualFile和class名生成java类, dir是文件夹名, 在这个文件夹里面新建文件
    public void createDir(VirtualFile dir, String ndirName) {
        new WriteAction<Void>() {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                VirtualFile temDirectory = dir.findChild(ndirName);
                if (temDirectory == null) {
                    PsiDirectoryFactory.getInstance(getProject()).createDirectory(dir.createChildDirectory(null, ndirName));
                }
            }
        }.execute();
    }

    /**
     * 根据virtualFile和class名生成java类, dir是文件夹名, 在这个文件夹里面新建文件
     * 可以生成interface之类的
     *
     * @param dir
     * @param nClassName
     */
    public void createJavaClass(VirtualFile dir, String nClassName) {
        new WriteAction<Void>() {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                VirtualFile child = dir.findChild(nClassName);
                if (child == null) {
                    PsiDirectory file = PsiManager.getInstance(getProject()).findDirectory(dir);
                    PsiClass newClass = JavaDirectoryService.getInstance().createClass(file, nClassName);
                }
            }
        }.execute();
    }

    public void createJavaInterface(VirtualFile dir, String nClassName) {
        new WriteAction<Void>() {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                VirtualFile child = dir.findChild(nClassName + ".java");
                if (child == null) {
                    PsiDirectory file = PsiManager.getInstance(getProject()).findDirectory(dir);
                    PsiClass newClass = JavaDirectoryService.getInstance().createInterface(file, nClassName);
                }
            }
        }.execute();
    }

    public void findAndCreateDir(String packageName) {
        String[] split = packageName.split("\\.");

        runWriteCommandAction((context) -> {
            VirtualFile parent;
            VirtualFile baseDir = getProject().getBaseDir();

            for (String s : split) {
                parent = baseDir;
                baseDir = baseDir.findChild(s);
                if (baseDir == null) {
                    try {
                        PsiDirectoryFactory.getInstance(getProject()).createDirectory(parent.findOrCreateChildData(null, s));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * 在文件中查找类, 找到第一个就返回
     *
     * @return
     */
    public PsiClass findFirstFileClass() {
        PsiFile psiFile = context.getPsiFile();
        if (psiFile != null) {
            for (PsiElement item : psiFile.getChildren()) {
                if (item instanceof PsiClass)
                    return (PsiClass) item;
            }
        }
        return null;
    }

    public PsiClass findFirstFileClass(PsiFile psiFile) {
        if (psiFile != null) {
            for (PsiElement item : psiFile.getChildren()) {
                if (item instanceof PsiClass)
                    return (PsiClass) item;
            }
        }
        return null;
    }

    public interface RunAction {
        void run(Context context);
    }

    public void runWriteCommandAction(RunAction runnable) {
        WriteCommandAction.runWriteCommandAction(context.getProject(), () -> runnable.run(context));
    }


}
