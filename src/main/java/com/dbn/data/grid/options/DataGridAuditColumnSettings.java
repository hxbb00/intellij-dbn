package com.dbn.data.grid.options;

import com.dbn.common.options.BasicProjectConfiguration;
import com.dbn.common.options.setting.Settings;
import com.dbn.data.grid.options.ui.DataGridAuditColumnSettingsForm;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.dbn.common.util.Strings.cachedUpperCase;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class DataGridAuditColumnSettings extends BasicProjectConfiguration<DataGridSettings, DataGridAuditColumnSettingsForm> {
    private final List<String> columnNames = new ArrayList<>();
    private boolean showColumns = true;
    private boolean allowEditing = false;

    private transient Set<String> lookupCache = new HashSet<>();

    DataGridAuditColumnSettings(DataGridSettings parent) {
        super(parent);
    }

    /****************************************************
     *                      Custom                      *
     ****************************************************/

    public void setColumnNames(Collection<String> columnNames) {
        this.columnNames.clear();
        this.columnNames.addAll(columnNames);
        updateLookupCache(columnNames);
    }

    private void updateLookupCache(Collection<String> columnNames) {
        lookupCache = new HashSet<>();
        for (String columnName : columnNames) {
            lookupCache.add(cachedUpperCase(columnName));
        }
    }

    public boolean isAuditColumn(String columnName) {
        return columnName!= null && !lookupCache.isEmpty() && lookupCache.contains(cachedUpperCase(columnName));
    }

    public boolean isColumnVisible(String columnName) {
        return showColumns || columnName == null || lookupCache.isEmpty() || !lookupCache.contains(cachedUpperCase(columnName));
    }

    /****************************************************
     *                   Configuration                  *
     ****************************************************/
    @Override
    @NotNull
    public DataGridAuditColumnSettingsForm createConfigurationEditor() {
        return new DataGridAuditColumnSettingsForm(this);
    }

    @Override
    public String getConfigElementName() {
        return "audit-columns";
    }

    @Override
    public void readConfiguration(Element element) {
        this.columnNames.clear();
        StringTokenizer columnNames = new StringTokenizer(Settings.getString(element, "column-names", ""), ",");
        while (columnNames.hasMoreTokens()) {
            String columnName = cachedUpperCase(columnNames.nextToken().trim());
            this.columnNames.add(columnName);
        }
        updateLookupCache(this.columnNames);

        showColumns = Settings.getBoolean(element, "visible", showColumns);
        allowEditing = Settings.getBoolean(element, "editable", allowEditing);
    }

    @Override
    public void writeConfiguration(Element element) {
        StringBuilder buffer = new StringBuilder();
        for (String columnName : columnNames) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append(columnName);
        }
        Settings.setString(element, "column-names", buffer.toString());
        Settings.setBoolean(element, "visible", showColumns);
        Settings.setBoolean(element, "editable", allowEditing);

    }

}
