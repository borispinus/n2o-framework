package net.n2oapp.framework.config.metadata.compile.region;


import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.region.N2oPanelRegion;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.meta.region.PanelRegion;
import net.n2oapp.framework.config.metadata.compile.IndexScope;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import org.springframework.stereotype.Component;

/**
 * Компиляция региона в виде панелей.
 */
@Component
public class PanelRegionCompiler extends BaseRegionCompiler<PanelRegion, N2oPanelRegion> {
    @Override
    public Class<N2oPanelRegion> getSourceClass() {
        return N2oPanelRegion.class;
    }

    @Override
    public PanelRegion compile(N2oPanelRegion source, PageContext context, CompileProcessor p) {
        PanelRegion region = new PanelRegion();
        build(region, source, context, p);
        region.setSrc("PanelRegion");
        region.setPlace(source.getPlace());
        region.setClassName(source.getClassName());
        region.setItems(initItems(source, context, p, PanelRegion.Panel.class));
        //  region.setColor();
        //region.setIcon();
        if (source.getWidgets() != null && source.getWidgets().length > 1)
            region.setHasTabs(true);
        else
            region.setHasTabs(false);
        region.setHeader(true);
        //  region.setFooterTitle();
        region.setOpen(true);
        region.setCollapsible(source.getCollapsible() != null ? source.getCollapsible() : true);
        region.setFullScreen(false);
        if (source.getTitle() == null && region.getItems().size() == 1) {
            region.setHeaderTitle(region.getItems().get(0).getLabel());
        } else {
            region.setHeaderTitle(source.getTitle());
        }
        return region;
    }

    @Override
    protected PanelRegion.Panel createItem(N2oWidget widget, IndexScope index, CompileProcessor p) {
        PanelRegion.Panel panel = new PanelRegion.Panel();
        panel.setIcon(widget.getIcon());
        panel.setLabel(widget.getName());
        panel.setId("panel" + index.get());
        panel.setOpened(p.cast(widget.getOpened(), true));
        panel.setProperties(p.mapAttributes(widget));
        return panel;
    }
}
