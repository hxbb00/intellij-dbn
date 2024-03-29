package com.dbn.common.component;


import com.dbn.common.dispose.StatefulDisposableBase;
import lombok.Getter;

@Getter
public abstract class ApplicationComponentBase extends StatefulDisposableBase implements ApplicationComponent {
    private final String componentName;

    public ApplicationComponentBase(String componentName) {
        this.componentName = componentName;
    }

}
