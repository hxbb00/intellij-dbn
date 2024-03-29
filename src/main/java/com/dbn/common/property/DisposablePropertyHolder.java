package com.dbn.common.property;

import com.dbn.common.dispose.StatefulDisposable;

public interface DisposablePropertyHolder<T extends Property.IntBase> extends PropertyHolder<T>, StatefulDisposable {

    @Override
    default boolean isDisposed() {
        return is(getDisposedProperty());
    }

    default void setDisposed(boolean disposed) {
        set(getDisposedProperty(), disposed);
    }

    T getDisposedProperty();
}
