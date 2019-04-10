package com.dci.intellij.dbn.common;

import com.dci.intellij.dbn.common.action.DBNDataKeys;
import com.dci.intellij.dbn.language.common.WeakRef;
import com.intellij.openapi.project.Project;

public class ProjectRef extends WeakRef<Project> {
    private ProjectRef(Project project) {
        super(project);
    }

    public static ProjectRef from(Project project) {
        if (project == null) {
            return new ProjectRef(null);
        } else {
            ProjectRef projectRef = project.getUserData(DBNDataKeys.PROJECT_REF);
            if (projectRef == null) {
                projectRef = new ProjectRef(project);
                project.putUserData(DBNDataKeys.PROJECT_REF, projectRef);
            }
            return projectRef;
        }
    }
}
