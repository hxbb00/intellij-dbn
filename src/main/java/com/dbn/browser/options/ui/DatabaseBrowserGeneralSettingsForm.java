package com.dbn.browser.options.ui;

import com.dbn.browser.options.BrowserDisplayMode;
import com.dbn.browser.options.DatabaseBrowserGeneralSettings;
import com.dbn.browser.options.listener.DisplayModeSettingsListener;
import com.dbn.browser.options.listener.ObjectDetailSettingsListener;
import com.dbn.common.event.ProjectEvents;
import com.dbn.common.options.SettingsChangeNotifier;
import com.dbn.common.options.ui.ConfigurationEditorForm;
import com.dbn.common.options.ui.ConfigurationEditorUtil;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.dbn.common.ui.util.ComboBoxes.*;

public class DatabaseBrowserGeneralSettingsForm extends ConfigurationEditorForm<DatabaseBrowserGeneralSettings> {
    private JPanel mainPanel;
    private JTextField navigationHistorySizeTextField;
    private JCheckBox showObjectDetailsCheckBox;
    private JComboBox<BrowserDisplayMode> browserTypeComboBox;


    public DatabaseBrowserGeneralSettingsForm(DatabaseBrowserGeneralSettings configuration) {
        super(configuration);

        initComboBox(browserTypeComboBox,
                BrowserDisplayMode.SIMPLE,
                BrowserDisplayMode.TABBED);

        resetFormChanges();
        registerComponent(mainPanel);
    }

    @NotNull
    @Override
    public JPanel getMainComponent() {
        return mainPanel;
    }

    @Override
    public void applyFormChanges() throws ConfigurationException {
        DatabaseBrowserGeneralSettings configuration = getConfiguration();
        ConfigurationEditorUtil.validateIntegerValue(navigationHistorySizeTextField, "Navigation history size", true, 0, 1000, "");

        final boolean repaintTree = configuration.isModified();
        
        final BrowserDisplayMode displayMode = getSelection(browserTypeComboBox);
        final boolean displayModeChanged = configuration.getDisplayMode() != displayMode;
        configuration.setDisplayMode(displayMode);


        configuration.getNavigationHistorySize().to(navigationHistorySizeTextField);
        configuration.getShowObjectDetails().to(showObjectDetailsCheckBox);

        Project project = configuration.getProject();
        SettingsChangeNotifier.register(() -> {
            if (displayModeChanged) {
                ProjectEvents.notify(project,
                        DisplayModeSettingsListener.TOPIC,
                        (listener) -> listener.displayModeChanged(displayMode));

            } else if (repaintTree) {
                ProjectEvents.notify(project,
                        ObjectDetailSettingsListener.TOPIC,
                        (listener) -> listener.displayDetailsChanged());
            }
        });
    }

    @Override
    public void resetFormChanges() {
        DatabaseBrowserGeneralSettings configuration = getConfiguration();
        setSelection(browserTypeComboBox, configuration.getDisplayMode());

        configuration.getNavigationHistorySize().from(navigationHistorySizeTextField);
        configuration.getShowObjectDetails().from(showObjectDetailsCheckBox);
    }

}
