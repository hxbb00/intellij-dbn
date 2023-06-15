package com.dci.intellij.dbn.editor.data.ui.table.listener;

import com.dci.intellij.dbn.common.ref.WeakRef;
import com.dci.intellij.dbn.common.ui.util.Keyboard.Key;
import com.dci.intellij.dbn.common.util.Messages;
import com.dci.intellij.dbn.data.type.DBDataType;
import com.dci.intellij.dbn.editor.data.model.DatasetEditorModel;
import com.dci.intellij.dbn.editor.data.model.DatasetEditorModelCell;
import com.dci.intellij.dbn.editor.data.ui.table.DatasetEditorTable;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.dci.intellij.dbn.common.dispose.Checks.isNotValid;
import static com.dci.intellij.dbn.common.dispose.Failsafe.conditionallyLog;
import static com.dci.intellij.dbn.editor.data.model.RecordStatus.INSERTING;
import static com.dci.intellij.dbn.editor.data.model.RecordStatus.UPDATING;

public class DatasetEditorKeyListener extends KeyAdapter {
    private final WeakRef<DatasetEditorTable> table;

    public DatasetEditorKeyListener(DatasetEditorTable table) {
        this.table = WeakRef.of(table);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isConsumed()) return;

        DatasetEditorTable table = getTable();
        if (isNotValid(table)) return;
        DatasetEditorModel model = table.getModel();

        int keyChar = e.getKeyChar();
        if (model.is(INSERTING)) {
            switch (keyChar) {
                case Key.ESCAPE:
                    model.cancelInsert(true);
                    break;
                case Key.ENTER:
                    int index = model.getInsertRowIndex();
                    try {
                        model.postInsertRecord(false, true, false);
                        if (model.isNot(INSERTING)) {
                            model.insertRecord(index + 1);
                        }
                    } catch (SQLException e1) {
                        conditionallyLog(e1);
                        Messages.showErrorDialog(table.getProject(), "Could not create row in " + table.getDataset().getQualifiedNameWithType() + ".", e1);
                    }
                    e.consume();
            }
        } else if (!table.isEditing()){
            if (keyChar == Key.DELETE) {
                int[] selectedRows = table.getSelectedRows();
                int[] selectedColumns = table.getSelectedColumns();
                table.performUpdate(-1, -1, () -> {
                    List<DatasetEditorModelCell> cells = new ArrayList<>();
                    for (int rowIndex : selectedRows) {
                        for (int columnIndex : selectedColumns) {
                            DatasetEditorModelCell cell = model.getCellAt(rowIndex, columnIndex);
                            if (cell != null) {
                                DBDataType dataType = cell.getColumnInfo().getDataType();
                                if (dataType.isNative() && !dataType.getNativeType().isLargeObject()) {
                                    cell.setTemporaryUserValue("");
                                    cell.set(UPDATING, true);
                                    cells.add(cell);
                                }
                            }
                        }
                    }
                    for (DatasetEditorModelCell cell : cells) {
                        cell.updateUserValue(null, true);
                    }

                });
            }
        }

    }

    public DatasetEditorTable getTable() {
        return table.get();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
