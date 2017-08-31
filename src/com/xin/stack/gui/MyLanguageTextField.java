/*
 * Copyright 2006 Sascha Weinreuter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xin.stack.gui;

import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.sql.psi.SqlLanguage;
import com.intellij.ui.EditorTextField;
import com.intellij.util.LocalTimeCounter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyLanguageTextField extends EditorTextField {
    private final Language myLanguage;
    // Could be null to allow usage in UI designer, as EditorTextField permits
    private final Project  myProject;


    public MyLanguageTextField(Language language, @Nullable Project project, @NotNull String value, @NotNull boolean isViewer) {
        this(language, project, value, isViewer, true);
    }

    public MyLanguageTextField(Language language, @Nullable Project project, @NotNull String value, @NotNull boolean isViewer, boolean oneLineMode) {
        this(language, project, value, isViewer, new SimpleDocumentCreator(), oneLineMode);
    }

    public MyLanguageTextField(@Nullable Language language,
                               @Nullable Project project,
                               @NotNull String value,
                               @NotNull boolean isViewer,
                               @NotNull DocumentCreator documentCreator) {
        this(language, project, value, isViewer, documentCreator, true);
    }

    public MyLanguageTextField(@Nullable Language language,
                               @Nullable Project project,
                               @NotNull String value,
                               @NotNull boolean isViewer,
                               @NotNull DocumentCreator documentCreator,
                               boolean oneLineMode) {
        super(documentCreator.createDocument(value, language, project), project,
              language != null ? language.getAssociatedFileType() : StdFileTypes.PLAIN_TEXT, isViewer, oneLineMode);

        myLanguage = language;
        myProject = project;

        setEnabled(language != null);
    }

    public interface DocumentCreator {
        Document createDocument(String value, @Nullable Language language, Project project);
    }

    public static class SimpleDocumentCreator implements DocumentCreator {
        @Override
        public Document createDocument(String value, @Nullable Language language, Project project) {
            return MyLanguageTextField.createDocument(value, language, project, this);
        }

        public void customizePsiFile(PsiFile file) {
        }
    }

    public static Document createDocument(String value, @Nullable Language language, Project project,
                                          @NotNull SimpleDocumentCreator documentCreator) {
        if (language != null) {
            final PsiFileFactory factory = PsiFileFactory.getInstance(project);
            final FileType fileType = language.getAssociatedFileType();
            assert fileType != null;

            final long stamp = LocalTimeCounter.currentTime();
            final PsiFile psiFile = factory.createFileFromText("Dummy." + fileType.getDefaultExtension(), fileType, value, stamp, true, false);
            documentCreator.customizePsiFile(psiFile);
            final Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
            assert document != null;
            return document;
        } else {
            return EditorFactory.getInstance().createDocument(value);
        }
    }

    @Override
    protected EditorEx createEditor() {
        final EditorEx ex = super.createEditor();

        if (myLanguage != null) {
//            final FileType fileType = myLanguage.getAssociatedFileType();

            SyntaxHighlighter mySQL = SyntaxHighlighterFactory.getSyntaxHighlighter(SqlLanguage.findLanguageByID("MySQL"), myProject, ex.getVirtualFile());
            ex.setHighlighter(HighlighterFactory.createHighlighter(mySQL, EditorColorsManager.getInstance().getGlobalScheme()));


        }
        ex.setEmbeddedIntoDialogWrapper(true);

        return ex;
    }
}
