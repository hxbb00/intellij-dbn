package com.dci.intellij.dbn.generator;

import com.dci.intellij.dbn.common.util.Naming;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.lookup.DBObjectRef;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AliasBundle {
    private final Map<DBObjectRef, String> aliases = new HashMap<>();

    public String getAlias(DBObject object) {
        DBObjectRef objectRef = object.getRef();
        String alias = aliases.get(objectRef);
        if (alias == null) {
            alias = Naming.createAliasName(object.getName());
            alias = getNextAvailable(alias);
            aliases.put(objectRef, alias);
        }
        return alias;
    }

    private String getNextAvailable(String alias) {
        for (String availableAlias : aliases.values()) {
            if (Objects.equals(alias, availableAlias)) {
                alias = Naming.nextNumberedIdentifier(alias, false);
            }
        }
        return alias;
    }

}
