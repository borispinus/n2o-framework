package net.n2oapp.framework.config.metadata.compile.widget;

import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oHtmlWidget;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.meta.widget.HtmlWidget;
import net.n2oapp.framework.config.metadata.compile.ParentRoteScope;
import org.springframework.stereotype.Component;

/**
 * Компиляция html виджета
 */
@Component
public class HtmlWidgetCompiler extends BaseWidgetCompiler<HtmlWidget, N2oHtmlWidget> {

    @Override
    public HtmlWidget compile(N2oHtmlWidget source, CompileContext<?,?> context, CompileProcessor p) {
        HtmlWidget widget = new HtmlWidget();
        CompiledObject object = getObject(source, p);
        compileWidget(widget, source, context, p, object);
        compileDataProviderAndRoutes(widget, source, p, null);
        WidgetScope widgetScope = new WidgetScope();
        widgetScope.setClientWidgetId(widget.getId());
        widgetScope.setWidgetId(source.getId());
        MetaActions widgetActions = new MetaActions();
        ParentRoteScope widgetRoute = initWidgetRoute(widget.getRoute(), context, p);
        widget.setUrl(source.getUrl());
        compileToolbarAndAction(widget, source, context, p, widgetScope, widgetRoute, widgetActions, object, null);
        return widget;
    }

    @Override
    protected String getPropertyWidgetSrc() {
        return "n2o.api.widget.html.src";
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oHtmlWidget.class;
    }
}
