package com.dbn.execution.statement.result;

import com.dbn.common.dispose.Failsafe;
import com.dbn.common.icon.Icons;
import com.dbn.common.message.MessageType;
import com.dbn.common.navigation.NavigationInstructions;
import com.dbn.connection.ConnectionHandler;
import com.dbn.connection.ConnectionId;
import com.dbn.connection.ConnectionRef;
import com.dbn.connection.SchemaId;
import com.dbn.database.DatabaseMessage;
import com.dbn.execution.ExecutionResultBase;
import com.dbn.execution.compiler.CompilerResult;
import com.dbn.execution.statement.StatementExecutionContext;
import com.dbn.execution.statement.StatementExecutionInput;
import com.dbn.execution.statement.StatementExecutionMessage;
import com.dbn.execution.statement.processor.StatementExecutionProcessor;
import com.dbn.execution.statement.result.ui.StatementExecutionResultForm;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.dbn.common.dispose.Disposer.replace;

public class StatementExecutionBasicResult extends ExecutionResultBase<StatementExecutionResultForm> implements StatementExecutionResult{
    private String name;
    private StatementExecutionMessage executionMessage;
    private StatementExecutionStatus executionStatus;
    private int executionDuration;
    private final int updateCount;
    private CompilerResult compilerResult;
    private String loggingOutput;
    private boolean loggingActive;

    private StatementExecutionProcessor executionProcessor;
    private final ConnectionRef connection;
    private final SchemaId databaseSchema;

    public StatementExecutionBasicResult(
            @NotNull StatementExecutionProcessor executionProcessor,
            @NotNull String name,
            int updateCount) {
        this.name = name;
        this.executionProcessor = executionProcessor;
        this.updateCount = updateCount;
        this.connection = Failsafe.nn(executionProcessor.getConnection()).ref();
        this.databaseSchema = executionProcessor.getTargetSchema();
    }

    @Override
    public PsiFile createPreviewFile() {
        return getExecutionInput().createPreviewFile();
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String name, boolean sticky) {
        this.name = name;
        getExecutionProcessor().setResultName(name, sticky);
    }

    @Override
    public Icon getIcon() {
        return getExecutionProcessor().isDirty() ? Icons.STMT_EXEC_RESULTSET_ORPHAN : Icons.STMT_EXEC_RESULTSET;
    }

    @Override
    @NotNull
    public StatementExecutionProcessor getExecutionProcessor() {
        return Failsafe.nn(executionProcessor);
    }

    @Override
    public StatementExecutionMessage getExecutionMessage() {
        return executionMessage;
    }

    @Override
    @NotNull
    public StatementExecutionInput getExecutionInput() {
        return getExecutionProcessor().getExecutionInput();
    }

    @NotNull
    @Override
    public StatementExecutionContext getExecutionContext() {
        return getExecutionInput().getExecutionContext();
    }

    @Override
    public void navigateToEditor(NavigationInstructions instructions) {
          getExecutionProcessor().navigateToEditor(instructions);
    }

    @Override
    public int getExecutionDuration() {
        return executionDuration;
    }

    @Override
    public void calculateExecDuration() {
        this.executionDuration = (int) (System.currentTimeMillis() - getExecutionContext().getExecutionTimestamp());
    }

    @Override
    public StatementExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    @Override
    public void setExecutionStatus(StatementExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    @Override
    public int getUpdateCount() {
        return updateCount;
    }

    @Override
    public void updateExecutionMessage(MessageType messageType, String message, DatabaseMessage databaseMessage) {
        executionMessage = new StatementExecutionMessage(this, message, databaseMessage, messageType);
    }

    @Override
    public void updateExecutionMessage(MessageType messageType, String message) {
        executionMessage = new StatementExecutionMessage(this, message, null, messageType);
    }

    @Override
    public void clearExecutionMessage() {
        executionMessage = replace(executionMessage, null);
    }

    @Override
    @NotNull
    public Project getProject() {
        return getExecutionProcessor().getProject();
    }

    @Override
    public ConnectionId getConnectionId() {
        return getExecutionInput().getConnectionId();
    }

    @Override
    @NotNull
    public ConnectionHandler getConnection() {
        return connection.ensure();
    }

    @Nullable
    public SchemaId getDatabaseSchema() {
        return databaseSchema;
    }

    @Nullable
    @Override
    public StatementExecutionResultForm createForm() {
        return null;
    }

    @Override
    public CompilerResult getCompilerResult() {
        return compilerResult;
    }

    @Override
    public boolean hasCompilerResult() {
        return compilerResult != null;
    }

    @Override
    public boolean isBulkExecution() {
        return getExecutionInput().isBulkExecution();
    }

    public void setCompilerResult(CompilerResult compilerResult) {
        this.compilerResult = compilerResult;
    }

    @Override
    public String getLoggingOutput() {
        return loggingOutput;
    }

    @Override
    public void setLoggingOutput(String loggingOutput) {
        this.loggingOutput = loggingOutput;
    }

    @Override
    public boolean isLoggingActive() {
        return loggingActive;
    }

    @Override
    public void setLoggingActive(boolean loggingActive) {
        this.loggingActive = loggingActive;
    }

    @Override
    public void disposeInner() {
        executionProcessor = null;
        super.disposeInner();
    }
}
