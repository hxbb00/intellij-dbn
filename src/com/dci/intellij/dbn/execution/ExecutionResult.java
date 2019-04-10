package com.dci.intellij.dbn.execution;

import com.dci.intellij.dbn.common.dispose.Disposable;
import com.dci.intellij.dbn.common.util.DataProviderSupplier;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.connection.ConnectionId;
import com.dci.intellij.dbn.execution.common.result.ui.ExecutionResultForm;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public interface ExecutionResult<F extends ExecutionResultForm> extends Disposable, DataProviderSupplier {

    @Nullable
    F createForm();

    @Nullable
    default F getForm() {
        Project project = getProject();
        ExecutionManager executionManager = ExecutionManager.getInstance(project);
        return (F) executionManager.getResultForm(this);
    }

    @NotNull
    String getName();

    Icon getIcon();

    @NotNull
    Project getProject();

    ConnectionId getConnectionId();

    @NotNull
    ConnectionHandler getConnectionHandler();

    PsiFile createPreviewFile();
}
