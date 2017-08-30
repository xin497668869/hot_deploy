package com.xin.api;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;


/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class SimpleLineMarkerProvider extends RelatedItemLineMarkerProvider {

    NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(new ImageIcon("C:\\Users\\xin\\Desktop\\all (1).png")).
//                        setTargets(properties).
        setTooltipText("Navigate to a simple property");

    public SimpleLineMarkerProvider(NavigationGutterIconBuilder<PsiElement> builder) {
        System.out.println("@@@@@@@@@@@@@@");
        this.builder = builder;
    }

    public SimpleLineMarkerProvider() {
        System.out.println("sdasd");
    }

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
//        super.collectNavigationMarkers(element, result);
        result.add(builder.createLineMarkerInfo(element));
        if (element instanceof PsiLiteralExpression) {
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
            String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
            if (value != null && value.startsWith("simple" + ":")) {
                result.add(builder.createLineMarkerInfo(element));
            }
        }
        //    @Override
//    protected void collectNavigationMarkers(@NotNull PsiElement element,
//                                            JamAnnotationAttributeMeta.Collection<? super RelatedItemLineMarkerInfo> result) {
//
//        result.add(builder.createLineMarkerInfo(element));
//        if (element instanceof PsiLiteralExpression) {
//            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
//            String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
//            if (value != null && value.startsWith("simple" + ":")) {
//
//                if (properties.size() > 0) {
//                    NavigationGutterIconBuilder<PsiElement> builder =
//                            NavigationGutterIconBuilder.create(SimpleIcons.FILE).
//                                    setTargets(properties).
//                                    setTooltipText("Navigate to a simple property");
//                    result.add(builder.createLineMarkerInfo(element));
//                }
//            }
//    }


    }
}
