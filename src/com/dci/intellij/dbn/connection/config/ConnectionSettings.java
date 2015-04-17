package com.dci.intellij.dbn.connection.config;

import java.util.UUID;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import com.dci.intellij.dbn.common.options.CompositeProjectConfiguration;
import com.dci.intellij.dbn.common.options.Configuration;
import com.dci.intellij.dbn.connection.config.ui.ConnectionSettingsForm;

public class ConnectionSettings extends CompositeProjectConfiguration<ConnectionSettingsForm> {
    private ConnectionBundleSettings parent;

    private String connectionId;
    private boolean isNew;

    private ConnectionDatabaseSettings databaseSettings;
    private ConnectionSshTunnelSettings sshTunnelSettings;
    private ConnectionDetailSettings detailSettings;
    private ConnectionFilterSettings filterSettings;

    public ConnectionSettings(ConnectionBundleSettings parent) {
        super(parent.getProject());
        this.parent = parent;
        databaseSettings = new GenericConnectionDatabaseSettings(this);
        sshTunnelSettings = new ConnectionSshTunnelSettings(this);
        detailSettings = new ConnectionDetailSettings(this);
        filterSettings = new ConnectionFilterSettings(this);
    }

    public ConnectionBundleSettings getParent() {
        return parent;
    }

    public ConnectionDatabaseSettings getDatabaseSettings() {
        return databaseSettings;
    }

    public ConnectionSshTunnelSettings getSshTunnelSettings() {
        return sshTunnelSettings;
    }

    public ConnectionDetailSettings getDetailSettings() {
        return detailSettings;
    }

    public ConnectionFilterSettings getFilterSettings() {
        return filterSettings;
    }

    @Override
    protected Configuration[] createConfigurations() {
        return new Configuration[] {
                databaseSettings,
                sshTunnelSettings,
                detailSettings,
                filterSettings};
    }

    public void generateNewId() {
        connectionId =  UUID.randomUUID().toString();
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    @NotNull
    @Override
    protected ConnectionSettingsForm createConfigurationEditor() {
        return new ConnectionSettingsForm(this);
    }

    public String getConnectionId() {
        return connectionId;
    }

    @Override
    public void readConfiguration(Element element) {
        if (ConnectionBundleSettings.IS_IMPORT_EXPORT_ACTION.get()) {
            generateNewId();
        } else {
            connectionId = element.getAttributeValue("id");
        }
        super.readConfiguration(element);
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public void writeConfiguration(Element element) {
        element.setAttribute("id", connectionId);
        super.writeConfiguration(element);
    }

    public ConnectionSettings clone() {
        try {
            Element connectionElement = new Element("Connection");
            writeConfiguration(connectionElement);
            ConnectionSettings clone = new ConnectionSettings(parent);
            clone.readConfiguration(connectionElement);
            clone.databaseSettings.setConnectivityStatus(databaseSettings.getConnectivityStatus());
            clone.generateNewId();
            return clone;
        } catch (Exception e) {
            return null;
        }

    }
}
