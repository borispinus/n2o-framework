package net.n2oapp.framework.config.metadata.compile.toolbar.table;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.event.action.N2oCustomAction;
import net.n2oapp.framework.api.metadata.event.action.N2oRefresh;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oButton;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.N2oToolbar;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

/**
 * Внутренняя утилита для генерации кнопок таблицы
 */
public class TableSettingsGeneratorUtil {

    public static N2oButton generateColumns(CompileProcessor p) {
        N2oButton columnsButton = new N2oButton();
        columnsButton.setDescription(p.getMessage("n2o.api.action.toolbar.button.columns.description"));
        columnsButton.setIcon("fa fa-table");
        columnsButton.setDropdownSrc(p.resolveText(property("n2o.api.action.columns.dropdownSrc")));
        columnsButton.setModel(ReduxModel.FILTER);
        return columnsButton;
    }

    public static N2oButton generateFilters(N2oToolbar toolbar, CompileProcessor p) {
        String widgetId = toolbar.getTargetWidgetId();
        if (widgetId == null) {
            WidgetScope widgetScope = p.getScope(WidgetScope.class);
            widgetId = widgetScope == null ? null : widgetScope.getClientWidgetId();
        }
        N2oButton filterButton = new N2oButton();
        filterButton.setDescription(p.getMessage("n2o.api.action.toolbar.button.filter.description"));
        filterButton.setIcon("fa fa-filter");
        N2oCustomAction filterAction = new N2oCustomAction();
        filterButton.setWidgetId(widgetId);
        Map<String, Object> props = new HashMap<>();
        props.put("type", p.resolveText(property("n2o.api.action.filters.type")));
        props.put("payload", Collections.singletonMap("widgetId", widgetId));
        filterAction.setProperties(props);
        filterButton.setAction(filterAction);
        filterButton.setModel(ReduxModel.FILTER);
        return filterButton;
    }

    public static N2oButton generateRefresh(CompileProcessor p) {
        N2oButton refreshButton = new N2oButton();
        refreshButton.setDescription(p.getMessage("n2o.api.action.toolbar.button.refresh.description"));
        refreshButton.setIcon("fa fa-refresh");
        N2oRefresh refreshAction = new N2oRefresh();
        refreshButton.setAction(refreshAction);
        refreshButton.setModel(ReduxModel.FILTER);
        return refreshButton;
    }

    public static N2oButton generateResize(CompileProcessor p) {
        N2oButton resizeButton = new N2oButton();
        resizeButton.setDescription(p.getMessage("n2o.api.action.toolbar.button.resize.description"));
        resizeButton.setIcon("fa fa-bars");
        resizeButton.setDropdownSrc(p.resolveText(property("n2o.api.action.resize.dropdownSrc")));
        resizeButton.setModel(ReduxModel.FILTER);
        return resizeButton;
    }

}
