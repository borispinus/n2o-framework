package net.n2oapp.framework.config.metadata.compile.page;

import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.meta.BindLink;
import net.n2oapp.framework.api.metadata.meta.ModelLink;
import net.n2oapp.framework.api.metadata.meta.Page;
import net.n2oapp.framework.api.metadata.meta.PageRoutes;
import net.n2oapp.framework.config.metadata.compile.BaseMetadataBinder;
import net.n2oapp.framework.config.metadata.compile.redux.Redux;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовое связывание данных на странице
 */
@Component
public class PageBinder implements BaseMetadataBinder<Page> {
    @Override
    public Page bind(Page page, CompileProcessor p) {
        if (page.getWidgets() != null) {
            page.getWidgets().values().forEach(p::bind);
        }
        if (page.getActions() != null)
            page.getActions().values().forEach(p::bind);
        if (page.getRoutes() != null) {
            Map<String, BindLink> pathMappings = new HashMap<>();
            page.getRoutes().getPathMapping().forEach((k, v) -> {
                pathMappings.put(k, Redux.createBindLink(v));
            });
            for (PageRoutes.Route route : page.getRoutes().getList()) {
                route.setPath(p.resolveUrl(route.getPath(), pathMappings, null));
            }
        }
        if (page.getBreadcrumb() != null)
            page.getBreadcrumb().stream().filter(b -> b.getPath() != null)
                    .forEach(b -> b.setPath(p.resolveUrl(b.getPath(), null, null)));
        if (page.getModels() != null) {
            page.getModels().values().forEach(bl -> {
                if (bl.getValue() instanceof String) {
                    bl.setValue(p.resolveText((String) bl.getValue()));
                }
            });
            resolveLinks(page.getModels(), p);
        }
        return page;
    }

    private void resolveLinks(Map<String, ModelLink> linkMap, CompileProcessor p) {
        linkMap.keySet().forEach(param ->
                linkMap.put(param, p.resolveLink(linkMap.get(param)))
        );
    }

    @Override
    public Class<? extends Compiled> getCompiledClass() {
        return Page.class;
    }
}
