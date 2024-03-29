package com.dbn.editor.data.model;

import com.dbn.common.property.DisposablePropertyHolder;
import com.dbn.common.property.PropertyHolderBase;

public abstract class RecordStatusHolder extends PropertyHolderBase.IntStore<RecordStatus> implements DisposablePropertyHolder<RecordStatus> {
    public final boolean isModified() {
        return is(RecordStatus.MODIFIED);
    }

    public final void setModified(boolean modified) {
        set(RecordStatus.MODIFIED, modified);
    }

    @Override
    public final RecordStatus getDisposedProperty() {
        return RecordStatus.DISPOSED;
    }

    public abstract void disposeInner();
}
