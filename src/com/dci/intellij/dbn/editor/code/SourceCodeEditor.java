package com.dci.intellij.dbn.editor.code;

import com.dci.intellij.dbn.common.editor.BasicTextEditorImpl;
import com.dci.intellij.dbn.common.event.EventManager;
import com.dci.intellij.dbn.common.thread.ConditionalLaterInvocator;
import com.dci.intellij.dbn.common.util.DocumentUtil;
import com.dci.intellij.dbn.language.common.psi.BasePsiElement;
import com.dci.intellij.dbn.language.common.psi.PsiUtil;
import com.dci.intellij.dbn.language.psql.PSQLFile;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.common.DBSchemaObject;
import com.dci.intellij.dbn.object.factory.ObjectFactoryListener;
import com.dci.intellij.dbn.object.lookup.DBObjectRef;
import com.dci.intellij.dbn.vfs.SourceCodeVirtualFile;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

public class SourceCodeEditor extends BasicTextEditorImpl<SourceCodeVirtualFile>{
    private DBObjectRef<DBSchemaObject> objectRef;
    private SourceCodeOffsets offsets;

    public SourceCodeEditor(Project project, SourceCodeVirtualFile sourceCodeFile, String name) {
        super(project, sourceCodeFile, name);

        objectRef = DBObjectRef.from(sourceCodeFile.getObject());
        Document document = this.textEditor.getEditor().getDocument();
        if (document.getTextLength() > 0) {
            offsets = sourceCodeFile.getOffsets();
            int guardedBlockEndOffset = offsets.getGuardedBlockEndOffset();
            if (guardedBlockEndOffset > 0) {
                DocumentUtil.createGuardedBlock(document, 0, guardedBlockEndOffset, null
                        /*"You are not allowed to change the name of the " + object.getTypeName()*/);
            }
        }
        EventManager.subscribe(project, ObjectFactoryListener.TOPIC, objectFactoryListener);
    }

    public DBSchemaObject getObject() {
        return DBObjectRef.get(objectRef);
    }

    public int getHeaderEndOffset() {
        return offsets.getHeaderEndOffset();
    }

    public void navigateTo(DBObject object) {
        PsiFile file = PsiUtil.getPsiFile(getObject().getProject(), getVirtualFile());
        if (file instanceof PSQLFile) {
            PSQLFile psqlFile = (PSQLFile) file;
            BasePsiElement navigable = psqlFile.lookupObjectDeclaration(object.getObjectType(), object.getName());
            if (navigable == null) navigable = psqlFile.lookupObjectSpecification(object.getObjectType(), object.getName());
            if (navigable != null) navigable.navigate(true);
        }
    }

    /********************************************************
     *                ObjectFactoryListener                 *
     ********************************************************/
    private ObjectFactoryListener objectFactoryListener = new ObjectFactoryListener() {
        public void objectCreated(DBSchemaObject object) {
        }

        public void objectDropped(DBSchemaObject object) {
            if (objectRef.is(object)) {
                new ConditionalLaterInvocator() {
                    public void execute() {
                        FileEditorManager fileEditorManager = FileEditorManager.getInstance(getProject());
                        fileEditorManager.closeFile(getVirtualFile().getDatabaseFile());
                    }
                }.start();
            }

        }    };


    @Override
    public void dispose() {
        EventManager.unsubscribe(objectFactoryListener);
        super.dispose();
    }
}
