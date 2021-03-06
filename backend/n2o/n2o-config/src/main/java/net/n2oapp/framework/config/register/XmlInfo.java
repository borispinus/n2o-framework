package net.n2oapp.framework.config.register;

import net.n2oapp.framework.api.metadata.SourceMetadata;
import net.n2oapp.framework.api.metadata.local.context.CompileContext;
import net.n2oapp.framework.api.reader.SourceLoader;
import net.n2oapp.framework.config.reader.XmlMetadataLoader;
import net.n2oapp.framework.config.register.storage.PathUtil;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Информация о метаданной
 */
public class XmlInfo extends FileInfo {
    protected boolean override;
    @Deprecated protected ConfigId configId;
    @Deprecated protected CompileContext context;
    protected Set<ConfigId> dependents = new HashSet<>();//ссылки на контекстныальные метаданные, которые нужно сбросить, при изменении этого файла
    @Deprecated protected Origin origin = Origin.xml;
    protected XmlInfo ancestor;

    @Deprecated
    protected XmlInfo() {

    }

    public XmlInfo(String id, Class<? extends SourceMetadata> baseSourceClass, String localPath, String configPath) {
        this.id = id;
        this.baseSourceClass = baseSourceClass;
        this.localPath = localPath;
        this.uri = PathUtil.concatAbsoluteAndLocalPath(configPath, localPath);
    }

    public XmlInfo(String id, Class<? extends SourceMetadata> baseSourceClass, String localPath) {
        this(id, baseSourceClass, localPath, "");
    }

    @Deprecated
    public XmlInfo(InfoConstructor constructor) {
        this.id = constructor.getId();
        this.baseSourceClass = constructor.getBaseSourceClass();
        this.uri = constructor.getUri();
        this.localPath = constructor.getLocalPath();
        configId = constructor.getConfigId();
        context = constructor.getContext();
        dependents = Collections.unmodifiableSet(constructor.getDependents());
        origin = constructor.getOrigin();
        ancestor = constructor.getAncestor();
    }

    @Deprecated
    public XmlInfo(ConfigId configId) {
        this.id = configId.getId();
        this.baseSourceClass = configId.getBaseSourceClass();
        this.configId = configId;
    }

    public String getType() {
        return configId.getType();
    }

    public String getKey() {
        return CacheControl.Key.createSourceKey(getId(), getBaseSourceClass());
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public static Class getClass(String cacheKey) {
        try {
            return Class.forName(cacheKey.split("\\$")[0]);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getId(String cacheKey) {
        return cacheKey.split("\\$")[1];
    }

    public CacheControl.Key getKey(String contextId) {
        return new CacheControl.Key(null, contextId, getKey());
    }

    public XmlInfo getAncestor() {
        return ancestor;
    }

    public String getURI() {
        return this.uri;
    }

    public Origin getOrigin() {
        return origin;
    }

    @SuppressWarnings("unchecked")
    public <C extends CompileContext> C getContext() {
        return (C) context;
    }

    @Override
    public Class<? extends SourceLoader> getReaderClass() {
        return XmlMetadataLoader.class;
    }

    public ConfigId getConfigId() {
        return configId;
    }

    public Set<ConfigId> getDependents() {
        return dependents;
    }

    public String getDirectory() {
        int idx = getLocalPath().indexOf(getFileName());
        if (idx > 0)
            return getLocalPath().substring(0, idx - 1);
        return "";
    }

    public boolean isOverride() {
        return override;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XmlInfo)) return false;
        XmlInfo info = (XmlInfo) o;
        return Objects.equals(id, info.id) &&
                Objects.equals(baseSourceClass, info.baseSourceClass) &&
                Objects.equals(localPath, info.localPath) &&
                Objects.equals(uri, info.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseSourceClass, localPath, uri);
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
