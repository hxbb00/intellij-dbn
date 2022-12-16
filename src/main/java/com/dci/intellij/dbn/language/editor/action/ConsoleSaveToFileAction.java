package com.dci.intellij.dbn.language.editor.action;

import com.dci.intellij.dbn.common.Icons;
import com.dci.intellij.dbn.common.action.Lookups;
import com.dci.intellij.dbn.common.action.ProjectAction;
import com.dci.intellij.dbn.common.thread.Write;
import com.dci.intellij.dbn.common.util.Documents;
import com.dci.intellij.dbn.common.util.Messages;
import com.dci.intellij.dbn.common.util.Titles;
import com.dci.intellij.dbn.connection.mapping.FileConnectionContextManager;
import com.dci.intellij.dbn.debugger.DatabaseDebuggerManager;
import com.dci.intellij.dbn.vfs.file.DBConsoleVirtualFile;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ConsoleSaveToFileAction extends ProjectAction {
    ConsoleSaveToFileAction() {
        super("Save to File", "Save console to file", Icons.CODE_EDITOR_SAVE_TO_FILE);
    }

    @Override
    protected void actionPerformed(@NotNull AnActionEvent e, @NotNull Project project) {
        VirtualFile virtualFile = Lookups.getVirtualFile(e);
        if (virtualFile instanceof DBConsoleVirtualFile) {
            final DBConsoleVirtualFile consoleVirtualFile = (DBConsoleVirtualFile) virtualFile;

            FileSaverDescriptor fileSaverDescriptor = new FileSaverDescriptor(
                    Titles.signed("Save Console to File"),
                    "Save content of the console \"" + consoleVirtualFile.getName() + "\" to file", "sql");

            FileSaverDialog fileSaverDialog = FileChooserFactory.getInstance().createSaveFileDialog(fileSaverDescriptor, project);
            Document document = Documents.getDocument(virtualFile);
            VirtualFileWrapper virtualFileWrapper = fileSaverDialog.save((VirtualFile) null, consoleVirtualFile.getName());
            if (document != null && virtualFileWrapper != null) {
                Write.run(() -> {
                    try {
                        VirtualFile newVirtualFile = virtualFileWrapper.getVirtualFile(true);
                        if (newVirtualFile != null) {
                            newVirtualFile.setBinaryContent(document.getCharsSequence().toString().getBytes());
                            FileConnectionContextManager contextManager = FileConnectionContextManager.getInstance(project);
                            contextManager.setConnection(newVirtualFile, consoleVirtualFile.getConnection());
                            contextManager.setDatabaseSchema(newVirtualFile, consoleVirtualFile.getSchemaId());
                            contextManager.setDatabaseSession(newVirtualFile, consoleVirtualFile.getSession());

                            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                            fileEditorManager.openFile(newVirtualFile, true);
                        }
                    } catch (IOException e1) {
                        Messages.showErrorDialog(project, "Error saving to file", "Could not save console content to file \"" + virtualFileWrapper.getFile().getName() + "\"", e1);
                    }

                });
            }
        }
    }

    @Override
    protected void update(@NotNull AnActionEvent e, @NotNull Project project) {
        VirtualFile virtualFile = Lookups.getVirtualFile(e);
        boolean visible = virtualFile instanceof DBConsoleVirtualFile && !DatabaseDebuggerManager.isDebugConsole(virtualFile);

        Presentation presentation = e.getPresentation();
        presentation.setEnabled(true);
        presentation.setVisible(visible);
    }
}