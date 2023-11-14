package com.dbn.execution.logging.action;

import com.dbn.common.action.ContextAction;
import com.dbn.common.action.DataKeys;
import com.dbn.execution.logging.DatabaseLoggingResult;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

abstract class AbstractDatabaseLoggingAction extends ContextAction<DatabaseLoggingResult> {
    AbstractDatabaseLoggingAction(String text, Icon icon) {
        super(text, null, icon);
    }

    @Nullable
    protected DatabaseLoggingResult getTarget(@NotNull AnActionEvent e) {
        return e.getData(DataKeys.DATABASE_LOG_OUTPUT);
    }
}