package com.dbn.object.impl;

import com.dbn.browser.ui.HtmlToolTipBuilder;
import com.dbn.common.icon.Icons;
import com.dbn.database.common.metadata.def.DBTriggerMetadata;
import com.dbn.object.DBDatabaseTrigger;
import com.dbn.object.DBSchema;
import com.dbn.object.common.status.DBObjectStatus;
import com.dbn.object.common.status.DBObjectStatusHolder;
import com.dbn.object.type.DBObjectType;
import com.dbn.object.type.DBTriggerEvent;
import com.dbn.object.type.DBTriggerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.sql.SQLException;

import static com.dbn.common.util.Strings.cachedLowerCase;

class DBDatabaseTriggerImpl extends DBTriggerImpl implements DBDatabaseTrigger {
    DBDatabaseTriggerImpl(DBSchema schema, DBTriggerMetadata metadata) throws SQLException {
        super(schema, metadata);
    }

    @NotNull
    @Override
    public DBObjectType getObjectType() {
        return DBObjectType.DATABASE_TRIGGER;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        DBObjectStatusHolder objectStatus = getStatus();
        if (objectStatus.is(DBObjectStatus.VALID)) {
            if (objectStatus.is(DBObjectStatus.ENABLED)) {
                if (objectStatus.is(DBObjectStatus.DEBUG)) {
                    return Icons.DBO_DATABASE_TRIGGER_DEBUG;
                } else {
                    return Icons.DBO_DATABASE_TRIGGER;
                }
            } else {
                if (objectStatus.is(DBObjectStatus.DEBUG)) {
                    return Icons.DBO_DATABASE_TRIGGER_DISABLED_DEBUG;
                } else {
                    return Icons.DBO_DATABASE_TRIGGER_DISABLED;
                }
            }
        } else {
            if (objectStatus.is(DBObjectStatus.ENABLED)) {
                return Icons.DBO_DATABASE_TRIGGER_ERR;
            } else {
                return Icons.DBO_DATABASE_TRIGGER_ERR_DISABLED;
            }

        }
    }


    @Override
    public void buildToolTip(HtmlToolTipBuilder ttb) {
        DBTriggerType triggerType = getTriggerType();
        DBTriggerEvent[] triggeringEvents = getTriggerEvents();
        ttb.append(true, getObjectType().getName(), true);
        StringBuilder triggerDesc = new StringBuilder();
        triggerDesc.append(" - ");
        triggerDesc.append(cachedLowerCase(triggerType.getName()));
        triggerDesc.append(" ") ;

        for (DBTriggerEvent triggeringEvent : triggeringEvents) {
            if (triggeringEvent != triggeringEvents[0]) triggerDesc.append(" or ");
            triggerDesc.append(triggeringEvent.getName());
        }

        triggerDesc.append(" on database");

        ttb.append(false, triggerDesc.toString(), false);

        ttb.createEmptyRow();
        super.buildToolTip(ttb);
    }
}
