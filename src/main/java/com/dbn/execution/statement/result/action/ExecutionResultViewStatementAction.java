package com.dbn.execution.statement.result.action;

import com.dbn.common.dispose.Failsafe;
import com.dbn.common.icon.Icons;
import com.dbn.execution.common.ui.StatementViewerPopup;
import com.dbn.execution.statement.result.StatementExecutionCursorResult;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ExecutionResultViewStatementAction extends AbstractExecutionResultAction {
    public ExecutionResultViewStatementAction() {
        super("View SQL Statement", Icons.EXEC_RESULT_VIEW_STATEMENT);
    }

    @Override
    protected void actionPerformed(@NotNull AnActionEvent e, @NotNull Project project, @NotNull StatementExecutionCursorResult executionResult) {
        StatementViewerPopup statementViewer = new StatementViewerPopup(executionResult);
        statementViewer.show((Component) Failsafe.nn(e.getInputEvent()).getSource());
    }
}
