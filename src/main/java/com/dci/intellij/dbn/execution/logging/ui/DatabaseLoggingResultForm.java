package com.dci.intellij.dbn.execution.logging.ui;

import com.dci.intellij.dbn.common.dispose.Disposer;
import com.dci.intellij.dbn.common.util.Actions;
import com.dci.intellij.dbn.connection.ConnectionHandler;
import com.dci.intellij.dbn.execution.common.result.ui.ExecutionResultFormBase;
import com.dci.intellij.dbn.execution.logging.DatabaseLoggingResult;
import com.intellij.ide.actions.NextOccurenceToolbarAction;
import com.intellij.ide.actions.PreviousOccurenceToolbarAction;
import com.intellij.openapi.actionSystem.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class DatabaseLoggingResultForm extends ExecutionResultFormBase<DatabaseLoggingResult> {
    private JPanel mainPanel;
    private JPanel consolePanel;
    private JPanel actionsPanel;

    private final DatabaseLoggingResultConsole console;

    public DatabaseLoggingResultForm(@NotNull DatabaseLoggingResult loggingResult) {
        super(loggingResult);
        ConnectionHandler connection = loggingResult.getConnection();
        console = new DatabaseLoggingResultConsole(connection, loggingResult.getName(), false);
        consolePanel.add(console.getComponent(), BorderLayout.CENTER);

        ActionManager actionManager = ActionManager.getInstance();
        //ActionGroup actionGroup = (ActionGroup) actionManager.getAction("DBNavigator.ActionGroup.DatabaseLogOutput");
        DefaultActionGroup toolbarActions = (DefaultActionGroup) console.getToolbarActions();
        if (toolbarActions != null) {
            for (AnAction action : toolbarActions.getChildActionsOrStubs()) {
                if (action instanceof PreviousOccurenceToolbarAction || action instanceof NextOccurenceToolbarAction) {
                    toolbarActions.remove(action);
                }
            }

            toolbarActions.add(actionManager.getAction("DBNavigator.Actions.DatabaseLogOutput.KillProcess"), Constraints.FIRST);
            toolbarActions.add(actionManager.getAction("DBNavigator.Actions.DatabaseLogOutput.RerunProcess"), Constraints.FIRST);
            toolbarActions.add(actionManager.getAction("DBNavigator.Actions.DatabaseLogOutput.Close"), Constraints.FIRST);
            toolbarActions.add(actionManager.getAction("DBNavigator.Actions.DatabaseLogOutput.Settings"), Constraints.LAST);
            ActionToolbar actionToolbar = Actions.createActionToolbar(actionsPanel, "", false, toolbarActions);
            actionsPanel.add(actionToolbar.getComponent());
            actionToolbar.setTargetComponent(console.getToolbarContextComponent());
        }

        Disposer.register(this, console);
    }

    public DatabaseLoggingResultConsole getConsole() {
        return console;
    }

    @NotNull
    @Override
    public JPanel getMainComponent() {
        return mainPanel;
    }
}