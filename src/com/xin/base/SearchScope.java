package com.xin.base;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 */
public class SearchScope {

    private GlobalSearchScope allScope;
    private GlobalSearchScope projectScope;

    public SearchScope(Project project) {
        allScope = GlobalSearchScope.allScope(project);
        projectScope = GlobalSearchScope.projectScope(project);
    }

    public GlobalSearchScope getAllScope() {
        return allScope;
    }


    public GlobalSearchScope getProjectScope() {
        return projectScope;
    }

}
