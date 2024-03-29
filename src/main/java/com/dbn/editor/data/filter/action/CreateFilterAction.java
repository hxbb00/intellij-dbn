package com.dbn.editor.data.filter.action;

import com.dbn.common.action.GroupPopupAction;
import com.dbn.common.icon.Icons;
import com.dbn.editor.data.filter.ui.DatasetFilterList;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class CreateFilterAction extends GroupPopupAction {
    private DatasetFilterList filterList;

    public CreateFilterAction(DatasetFilterList filterList) {
        super("Create filter", "Create filter", Icons.ACTION_ADD);
        this.filterList = filterList;
    }

    @Override
    protected AnAction[] getActions(AnActionEvent e) {
        return new AnAction[]{
            new CreateBasicFilterAction(filterList),
            new CreateCustomFilterAction(filterList)
        };
    }

}
