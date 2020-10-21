package com.dci.intellij.dbn.common;

import com.dci.intellij.dbn.common.dispose.StatefulDisposable;
import com.dci.intellij.dbn.common.notification.NotificationSupport;
import com.dci.intellij.dbn.common.options.setting.SettingsSupport;
import com.dci.intellij.dbn.common.project.ProjectRef;
import com.dci.intellij.dbn.common.project.ProjectUtil;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractProjectComponent extends StatefulDisposable.Base implements
        SettingsSupport,
        ProjectComponent,
        ProjectManagerListener,
        StatefulDisposable,
        NotificationSupport {

    private final ProjectRef project;

    protected AbstractProjectComponent(Project project) {
        this.project = ProjectRef.of(project);
        ProjectManager projectManager = ProjectManager.getInstance();
        projectManager.addProjectManagerListener(project, this);
    }


    protected <T> void subscribe(Topic<T> topic, T handler) {
        Project project = getProject();
        MessageBus messageBus = project.getMessageBus();
        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(topic, handler);
    }

    @Override
    @NotNull
    public Project getProject() {
        return project.ensure();
    }

    public boolean canCloseProject() {
        return true;
    }

    public void projectClosing() {

    }

    /***********************************************
     *            ProjectManagerListener           *
     ***********************************************/
    @Override
    public final void projectOpened(@NotNull Project project) {}

    @Override
    public final void projectClosed(@NotNull Project project) {}

    @Override
    public final boolean canCloseProject(@NotNull Project project) {
        if (project.equals(getProject())) {
            return canCloseProject();
        }
        return true;
    }

    @Override
    public final void projectClosing(@NotNull Project project) {
        if (project.equals(getProject())) {
            projectClosing();
        }
    }


    @Override
    public void disposeComponent() {
        dispose();
    }

    @Override
    protected void disposeInner() {
        nullify();
    }

    protected void closeProject() {
        ProjectUtil.closeProject(getProject());
    }

    @Override
    public void checkDisposed() {
        super.checkDisposed();
        getProject();
    }
}
