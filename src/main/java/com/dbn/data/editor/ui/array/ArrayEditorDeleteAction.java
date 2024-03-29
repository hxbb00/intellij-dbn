package com.dbn.data.editor.ui.array;

import com.dbn.common.icon.Icons;
import com.dbn.data.editor.ui.TextFieldWithPopup;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

class ArrayEditorDeleteAction extends ArrayEditorAction {
    ArrayEditorDeleteAction() {
        super("Delete Content", null, Icons.TEXT_CELL_EDIT_DELETE);
        //setShortcutSet(Keyboard.createShortcutSet(KeyEvent.VK_DELETE, UserInterface.ctrlDownMask()));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ArrayEditorPopupProviderForm form = getArrayEditorForm(e);
        if (form == null) return;

        TextFieldWithPopup editorComponent = form.getEditorComponent();
        editorComponent.getUserValueHolder().updateUserValue(new ArrayList<String>(), false);
        form.hidePopup();
    }
}
