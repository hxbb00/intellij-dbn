package com.dbn.connection.transaction.ui;

import com.dbn.common.dispose.StatefulDisposableBase;
import com.dbn.common.ui.table.DBNReadonlyTableModel;
import com.dbn.common.util.Lists;
import com.dbn.connection.ConnectionHandler;
import com.dbn.connection.ConnectionRef;
import com.dbn.connection.ConnectionType;
import com.dbn.connection.jdbc.DBNConnection;
import com.dbn.connection.transaction.PendingTransaction;
import com.dbn.connection.transaction.PendingTransactionBundle;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PendingTransactionsTableModel extends StatefulDisposableBase implements DBNReadonlyTableModel {
    private final ConnectionRef connection;
    private final List<DBNConnection> connections;

    PendingTransactionsTableModel(ConnectionHandler connection) {
        this.connection = connection.ref();
        this.connections = connection.getConnections(
                ConnectionType.MAIN,
                ConnectionType.SESSION,
                ConnectionType.DEBUG,
                ConnectionType.DEBUGGER);
    }

    public ConnectionHandler getConnection() {
        return connection.ensure();
    }

    @NotNull
    public Project getProject() {
        return getConnection().getProject();
    }

    @NotNull
    public List<DBNConnection> getConnections() {
        return connections;
    }

    @NotNull
    public List<DBNConnection> getTransactionalConnections() {
        return Lists.filtered(connections, connection -> connection.getDataChanges() != null);
    }

    @Override
    public int getRowCount() {
        return (int) getRows().count();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return
            columnIndex == 0 ? "Session" :
            columnIndex == 1 ? "Source" :
            columnIndex == 2 ? "Details" : null ;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return PendingTransaction.class;
    }

    @Override
    public PendingTransaction getValueAt(int rowIndex, int columnIndex) {
        return getRows().collect(Collectors.toList()).get(rowIndex);
    }

    private Stream<PendingTransaction> getRows() {
        return connections.stream().flatMap(connection -> {
            PendingTransactionBundle dataChanges = connection.getDataChanges();
            return dataChanges == null ? Stream.empty() : dataChanges.getEntries().stream();
        });
    }

    @Override
    public void disposeInner() {
        connections.clear();
    }
}
