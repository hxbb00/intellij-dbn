package com.dci.intellij.dbn.common.content.loader;

import com.dci.intellij.dbn.common.content.DynamicContentElement;
import com.dci.intellij.dbn.common.content.DynamicContentType;
import com.dci.intellij.dbn.common.util.Commons;
import com.dci.intellij.dbn.common.util.Safe;
import com.dci.intellij.dbn.database.common.metadata.DBObjectMetadata;
import com.dci.intellij.dbn.object.DBSchema;
import com.dci.intellij.dbn.object.common.DBObject;
import com.dci.intellij.dbn.object.common.DBSchemaObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.dci.intellij.dbn.common.content.DynamicContentType.NULL;

@Slf4j
public abstract class DynamicContentLoaderImpl<
                T extends DynamicContentElement,
                M extends DBObjectMetadata>
        implements DynamicContentLoader<T, M>{

    private static final Map<DynamicContentType, Map<DynamicContentType, DynamicContentLoader>> LOADERS = new ConcurrentHashMap<>();

    public DynamicContentLoaderImpl(@Nullable DynamicContentType parentContentType, @NotNull DynamicContentType contentType, boolean register) {
        if (register) {
            register(parentContentType, contentType, this);
        }
    }

    private static void register(
            @Nullable DynamicContentType parentContentType,
            @NotNull DynamicContentType contentType,
            @NotNull DynamicContentLoader loader) {

        parentContentType = Commons.nvl(parentContentType, NULL);
        Map<DynamicContentType, DynamicContentLoader> childLoaders = LOADERS.computeIfAbsent(parentContentType, t -> new HashMap<>());
        DynamicContentLoader contentLoader = childLoaders.get(contentType);
        if (contentLoader == null) {
            childLoaders.put(contentType, loader);
        } else if (contentLoader != loader){
            log.error("Duplicate content loader registration for parentContentType={} and contentType={}", parentContentType, contentType);
        }
    }

    @NotNull
    public static <T extends DynamicContentElement, M extends DBObjectMetadata> DynamicContentLoader<T, M> resolve(
            @Nullable DynamicContentType<?> parentContentType,
            @NotNull DynamicContentType<?> contentType) {

        DynamicContentType<?> lookupParentContentType = Commons.nvl(parentContentType, NULL);
        while (lookupParentContentType != null) {
            Map<DynamicContentType, DynamicContentLoader> loaderMap = LOADERS.get(lookupParentContentType);
            if (loaderMap != null) {
                DynamicContentType<?> lookupContentType = contentType;
                while (lookupContentType != null) {
                    DynamicContentLoader loader = loaderMap.get(lookupContentType);
                    if (loader != null) {
                        return loader;
                    }
                    DynamicContentType<?> genericContentType = lookupContentType.getGenericType();
                    lookupContentType = genericContentType == lookupContentType ? null : genericContentType;
                }
            }
            DynamicContentType<?> genericParentContentType = lookupParentContentType.getGenericType();
            lookupParentContentType =
                    genericParentContentType == NULL ? null :
                    genericParentContentType == lookupParentContentType ? NULL :
                    genericParentContentType;
        }

        throw new UnsupportedOperationException("No entry found for content type "+ lookupParentContentType + " / " + contentType);
    }

    @Nullable
    protected static String getObjectName(DBObject object) {
        return Safe.call(object, o -> o.getName());
    }

    @Nullable
    protected static String getSchemaName(DBSchemaObject object) {
        DBSchema schema = object == null ? null : object.getSchema();
        return schema == null ? null : schema.getName();
    }
}