package com.dci.intellij.dbn.vfs;

import java.util.HashMap;
import java.util.Map;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.dci.intellij.dbn.common.AbstractProjectComponent;
import com.dci.intellij.dbn.common.event.EventManager;
import com.dci.intellij.dbn.object.common.DBSchemaObject;
import com.dci.intellij.dbn.object.lookup.DBObjectRef;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

@State(
    name = "DBNavigator.Project.DatabaseFileManager",
    storages = {
        @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/dbnavigator.xml", scheme = StorageScheme.DIRECTORY_BASED),
        @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/misc.xml", scheme = StorageScheme.DIRECTORY_BASED),
        @Storage(file = StoragePathMacros.PROJECT_FILE)}
)
public class DatabaseFileManager extends AbstractProjectComponent implements PersistentStateComponent<Element> {
    private Map<DBObjectRef, DatabaseEditableObjectVirtualFile> openFiles = new HashMap<DBObjectRef, DatabaseEditableObjectVirtualFile>();


    private DatabaseFileManager(Project project) {
        super(project);
    }
    public static DatabaseFileManager getInstance(Project project) {
        return project.getComponent(DatabaseFileManager.class);
    }

    public boolean isFileOpened(DBSchemaObject object) {
        return openFiles.containsKey(object.getRef());
    }

    /***************************************
     *            ProjectComponent         *
     ***************************************/
    @NotNull
    public String getComponentName() {
        return "DBNavigator.Project.DatabaseFileManager";
    }

    public void projectOpened() {
        EventManager.subscribe(getProject(), FileEditorManagerListener.FILE_EDITOR_MANAGER, fileEditorManagerListener);
    }

    public void projectClosed() {
        EventManager.unsubscribe(fileEditorManagerListener);
    }

    /*********************************************
     *            FileEditorManagerListener       *
     *********************************************/
    private FileEditorManagerListener fileEditorManagerListener  =new FileEditorManagerAdapter() {
        public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
            if (file instanceof DatabaseEditableObjectVirtualFile) {
                DatabaseEditableObjectVirtualFile databaseFile = (DatabaseEditableObjectVirtualFile) file;
                openFiles.put(databaseFile.getObjectRef(), databaseFile);
            }
        }

        public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
            if (file instanceof DatabaseEditableObjectVirtualFile) {
                DatabaseEditableObjectVirtualFile databaseFile = (DatabaseEditableObjectVirtualFile) file;
                openFiles.remove(databaseFile.getObjectRef());
            }
        }

        @Override
        public void selectionChanged(@NotNull FileEditorManagerEvent event) {

        }
    };

    /*********************************************
     *            PersistentStateComponent       *
     *********************************************/
    @Nullable
    @Override
    public Element getState() {
        return null;
    }

    @Override
    public void loadState(Element element) {

    }
}
