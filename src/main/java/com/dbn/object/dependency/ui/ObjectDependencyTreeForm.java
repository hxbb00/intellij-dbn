package com.dbn.object.dependency.ui;

import com.dbn.common.icon.Icons;
import com.dbn.common.ui.form.DBNFormBase;
import com.dbn.common.ui.form.DBNHeaderForm;
import com.dbn.common.ui.tree.Trees;
import com.dbn.common.util.Actions;
import com.dbn.object.common.DBObject;
import com.dbn.object.common.DBSchemaObject;
import com.dbn.object.dependency.ObjectDependencyManager;
import com.dbn.object.dependency.ObjectDependencyType;
import com.dbn.object.lookup.DBObjectRef;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.awt.*;

import static com.dbn.common.ui.util.ComboBoxes.*;

public class ObjectDependencyTreeForm extends DBNFormBase {
    private JPanel mainPanel;
    private JPanel actionsPanel;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private JComboBox<ObjectDependencyType> dependencyTypeComboBox;
    private JBScrollPane treeScrollPane;

    private final DBNHeaderForm headerForm;
    private final ObjectDependencyTree dependencyTree;

    private final DBObjectRef<DBSchemaObject> object;

    public ObjectDependencyTreeForm(ObjectDependencyTreeDialog parentComponent, final DBSchemaObject schemaObject) {
        super(parentComponent);
        Project project = ensureProject();
        dependencyTree = new ObjectDependencyTree(this, schemaObject) {
            @Override
            public void setModel(TreeModel model) {
                super.setModel(model);
                if (headerForm != null && model instanceof ObjectDependencyTreeModel) {
                    ObjectDependencyTreeModel dependencyTreeModel = (ObjectDependencyTreeModel) model;
                    ObjectDependencyTreeNode rootNode = dependencyTreeModel.getRoot();
                    DBObject object = rootNode.getObject();
                    if (object != null) {
                        headerForm.update(object);
                    }
                }
            }
        };
        treeScrollPane.setViewportView(dependencyTree);
        ObjectDependencyManager dependencyManager = ObjectDependencyManager.getInstance(project);
        ObjectDependencyType dependencyType = dependencyManager.getLastUserDependencyType();


        initComboBox(dependencyTypeComboBox, ObjectDependencyType.values());
        setSelection(dependencyTypeComboBox, dependencyType);
        dependencyTypeComboBox.addActionListener(e -> {
            ObjectDependencyType selection = getSelection(dependencyTypeComboBox);
            dependencyTree.setDependencyType(selection);
        });

        this.object = DBObjectRef.of(schemaObject);
        headerForm = new DBNHeaderForm(this, schemaObject);
        headerPanel.add(headerForm.getComponent(), BorderLayout.CENTER);

        ActionToolbar actionToolbar = Actions.createActionToolbar(actionsPanel,"", true,
                new PreviousSelectionAction(),
                new NextSelectionAction(),
                Actions.SEPARATOR,
                new ExpandTreeAction(),
                new CollapseTreeAction());
        actionsPanel.add(actionToolbar.getComponent(), BorderLayout.CENTER);
    }



    private DBSchemaObject getObject() {
        return DBObjectRef.get(object);
    }

    @NotNull
    @Override
    public JPanel getMainComponent() {
        return mainPanel;
    }

    public class ExpandTreeAction extends DumbAwareAction {

        ExpandTreeAction() {
            super("Expand All", null, Icons.ACTION_EXPAND_ALL);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Trees.expandAll(dependencyTree);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            Presentation presentation = e.getPresentation();
            presentation.setText("Expand All");
        }
    }

    public class PreviousSelectionAction extends DumbAwareAction {
        PreviousSelectionAction() {
            super("Previous Selection", null, Icons.BROWSER_BACK);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            DBObject previous = dependencyTree.getSelectionHistory().previous();
            dependencyTree.setRootObject((DBSchemaObject) previous, false);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            Presentation presentation = e.getPresentation();
            presentation.setEnabled(dependencyTree.getSelectionHistory().hasPrevious());
        }
    }

    public class NextSelectionAction extends DumbAwareAction {
        NextSelectionAction() {
            super("Next Selection", null, Icons.BROWSER_NEXT);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            DBObject next = dependencyTree.getSelectionHistory().next();
            dependencyTree.setRootObject((DBSchemaObject) next, false);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            Presentation presentation = e.getPresentation();
            presentation.setEnabled(dependencyTree.getSelectionHistory().hasNext());
        }
    }

    public class CollapseTreeAction extends DumbAwareAction {

        CollapseTreeAction() {
            super("Collapse All", null, Icons.ACTION_COLLAPSE_ALL);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Trees.collapseAll(dependencyTree);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            Presentation presentation = e.getPresentation();
            presentation.setText("Collapse All");
        }
    }
}
