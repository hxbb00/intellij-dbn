package com.dbn.browser.ui;

import com.dbn.browser.model.BrowserTreeNode;
import com.dbn.browser.model.TabbedBrowserTreeModel;
import com.dbn.browser.options.listener.ObjectDetailSettingsListener;
import com.dbn.common.dispose.Failsafe;
import com.dbn.common.event.ProjectEvents;
import com.dbn.common.ui.misc.DBNScrollPane;
import com.dbn.common.ui.util.UserInterface;
import com.dbn.connection.ConnectionHandler;
import com.dbn.connection.ConnectionId;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SimpleBrowserForm extends DatabaseBrowserForm{
    private JPanel mainPanel;
    private DBNScrollPane browserScrollPane;
    private final DatabaseBrowserTree browserTree;

    public SimpleBrowserForm(@NotNull TabbedBrowserForm parent, @NotNull ConnectionHandler connection) {
        super(parent);
        browserTree = createBrowserTree(connection);
    }

    public SimpleBrowserForm(@NotNull BrowserToolWindowForm parent) {
        super(parent);
        browserTree = createBrowserTree(null);
    }

    @NotNull
    private DatabaseBrowserTree createBrowserTree(@Nullable ConnectionHandler connection) {
        DatabaseBrowserTree browserTree = new DatabaseBrowserTree(this, connection);
        browserScrollPane.setViewportView(browserTree);
        browserScrollPane.setBorder(JBUI.Borders.emptyTop(1));
        ToolTipManager.sharedInstance().registerComponent(browserTree);

        ProjectEvents.subscribe(ensureProject(), this, ObjectDetailSettingsListener.TOPIC, objectDetailSettingsListener());
        return browserTree;
    }

    @NotNull
    private ObjectDetailSettingsListener objectDetailSettingsListener() {
        return () -> UserInterface.repaint(browserTree);
    }

    @Nullable
    public ConnectionId getConnectionId(){
        ConnectionHandler connection = getConnection();
        return connection == null ? null : connection.getConnectionId();
    }

    @Nullable
    public ConnectionHandler getConnection(){
        DatabaseBrowserTree browserTree = getBrowserTree();
        if (browserTree.getModel() instanceof TabbedBrowserTreeModel) {
            TabbedBrowserTreeModel treeModel = (TabbedBrowserTreeModel) browserTree.getModel();
            return treeModel.getConnection();
        }
        return null;
    }

    @NotNull
    @Override
    public JPanel getMainComponent() {
        return mainPanel;
    }

    @Override
    @NotNull
    public DatabaseBrowserTree getBrowserTree() {
        return Failsafe.nn(browserTree);
    }

    @Override
    public void selectElement(BrowserTreeNode treeNode, boolean focus, boolean scroll) {
        getBrowserTree().selectElement(treeNode, focus);
    }

    @Override
    public void rebuildTree() {
        getBrowserTree().getModel().getRoot().rebuildTreeChildren();
    }

}
