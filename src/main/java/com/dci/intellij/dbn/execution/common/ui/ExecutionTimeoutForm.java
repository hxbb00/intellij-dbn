package com.dci.intellij.dbn.execution.common.ui;

import com.dci.intellij.dbn.common.action.GroupPopupAction;
import com.dci.intellij.dbn.common.icon.Icons;
import com.dci.intellij.dbn.common.ui.form.DBNForm;
import com.dci.intellij.dbn.common.ui.form.DBNFormBase;
import com.dci.intellij.dbn.common.util.Actions;
import com.dci.intellij.dbn.debugger.DBDebuggerType;
import com.dci.intellij.dbn.execution.ExecutionInput;
import com.dci.intellij.dbn.execution.common.options.ExecutionTimeoutSettings;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.dci.intellij.dbn.common.ui.util.TextFields.onTextChange;
import static com.dci.intellij.dbn.diagnostics.Diagnostics.conditionallyLog;

public abstract class ExecutionTimeoutForm extends DBNFormBase {
    private JTextField executionTimeoutTextField;
    private JPanel mainPanel;
    private JPanel actionsPanel;
    private JLabel hintLabel;

    private boolean hasErrors;
    private transient int timeout;

    private final ExecutionInput executionInput;
    private final DBDebuggerType debuggerType;

    protected ExecutionTimeoutForm(DBNForm parent, ExecutionInput executionInput, DBDebuggerType debuggerType) {
        super(parent);
        this.executionInput = executionInput;
        this.debuggerType = debuggerType;

        timeout = getInputTimeout();
        executionTimeoutTextField.setText(String.valueOf(timeout));
        executionTimeoutTextField.setForeground(timeout == getSettingsTimeout() ?
                UIUtil.getLabelDisabledForeground() :
                UIUtil.getTextFieldForeground());


        onTextChange(executionTimeoutTextField, e -> updateErrorMessage());

        ActionToolbar actionToolbar = Actions.createActionToolbar(
                actionsPanel,
                "DBNavigator.Place.ExecutionTimeoutForm.Settings", true,
                new SettingsAction());

        actionsPanel.add(actionToolbar.getComponent(), BorderLayout.CENTER);
    }

    private void updateErrorMessage() {
        String text = executionTimeoutTextField.getText();
        try {
            timeout = Integer.parseInt(text);
            executionTimeoutTextField.setForeground(timeout == getSettingsTimeout() ?
                    UIUtil.getLabelDisabledForeground() :
                    UIUtil.getTextFieldForeground());

            if (debuggerType.isDebug())
                executionInput.setDebugExecutionTimeout(timeout); else
                executionInput.setExecutionTimeout(timeout);
            hintLabel.setIcon(null);
            hintLabel.setToolTipText(null);
            hasErrors = false;
            handleChange(false);
        } catch (NumberFormatException e1) {
            conditionallyLog(e1);
            //errorLabel.setText("Timeout must be an integer");
            hintLabel.setIcon(Icons.COMMON_ERROR);
            hintLabel.setToolTipText("Timeout must be an integer");
            hasErrors = true;
            handleChange(true);
        }
    }

    private int getInputTimeout() {
        return debuggerType.isDebug() ?
                    executionInput.getDebugExecutionTimeout() :
                    executionInput.getExecutionTimeout();
    }

    private int getSettingsTimeout() {
        ExecutionTimeoutSettings timeoutSettings = executionInput.getExecutionTimeoutSettings();
        return debuggerType.isDebug() ?
                timeoutSettings.getDebugExecutionTimeout() :
                timeoutSettings.getExecutionTimeout();
    }

    protected void handleChange(boolean hasError){}

    @NotNull
    @Override
    public JPanel getMainComponent() {
        return mainPanel;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public class SettingsAction extends GroupPopupAction {
        SettingsAction() {
            super("Settings", null, Icons.ACTION_OPTIONS_MENU);
        }
        @Override
        protected AnAction[] getActions(AnActionEvent e) {
            return new AnAction[]{
                    new SaveToSettingsAction(),
                    new ReloadDefaultAction()
            };
        }

        @Override
        protected void update(@NotNull AnActionEvent e, @NotNull Project project) {
            Presentation presentation = e.getPresentation();
            presentation.setEnabled(!hasErrors && timeout != getSettingsTimeout());
        }
    }

    class SaveToSettingsAction extends AnAction {
        SaveToSettingsAction() {
            super("Save to Settings");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            ExecutionTimeoutSettings timeoutSettings = executionInput.getExecutionTimeoutSettings();
            String text = executionTimeoutTextField.getText();
            int timeout = Integer.parseInt(text);

            if (debuggerType.isDebug())
                timeoutSettings.setDebugExecutionTimeout(timeout); else
                timeoutSettings.setExecutionTimeout(timeout);
        }

        @Override
        public void update(AnActionEvent e) {
            Presentation presentation = e.getPresentation();
            presentation.setEnabled(!hasErrors);
        }
    }

    class ReloadDefaultAction extends AnAction {

        ReloadDefaultAction() {
            super("Reload from Settings");
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            int timeout = getSettingsTimeout();
            executionTimeoutTextField.setText(String.valueOf(timeout));
        }

        @Override
        public void update(AnActionEvent e) {
            Presentation presentation = e.getPresentation();
            presentation.setEnabled(!hasErrors);
        }
    }

}
