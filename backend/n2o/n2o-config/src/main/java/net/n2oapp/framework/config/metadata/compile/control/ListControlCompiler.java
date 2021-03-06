package net.n2oapp.framework.config.metadata.compile.control;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.control.N2oListField;
import net.n2oapp.framework.api.metadata.global.dao.N2oPreFilter;
import net.n2oapp.framework.api.metadata.global.dao.N2oQuery;
import net.n2oapp.framework.api.metadata.local.CompiledQuery;
import net.n2oapp.framework.api.metadata.local.util.StrictMap;
import net.n2oapp.framework.api.metadata.meta.BindLink;
import net.n2oapp.framework.api.metadata.meta.ModelLink;
import net.n2oapp.framework.api.metadata.meta.control.DefaultValues;
import net.n2oapp.framework.api.metadata.meta.control.ListControl;
import net.n2oapp.framework.api.metadata.meta.control.StandardField;
import net.n2oapp.framework.api.metadata.meta.widget.WidgetDataProvider;
import net.n2oapp.framework.api.script.ScriptProcessor;
import net.n2oapp.framework.config.metadata.compile.context.QueryContext;
import net.n2oapp.framework.config.metadata.compile.widget.ModelsScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

public abstract class ListControlCompiler<T extends ListControl, S extends N2oListField> extends StandardFieldCompiler<T, S> {

    protected StandardField<T> compileListControl(T listControl, S source, CompileContext<?, ?> context, CompileProcessor p) {
        listControl.setFormat(p.resolveJS(source.getFormat()));
        listControl.setLabelFieldId(p.cast(p.resolveJS(source.getLabelFieldId()), "name"));
        listControl.setValueFieldId(p.cast(p.resolveJS(source.getValueFieldId()), "id"));
        listControl.setIconFieldId(p.resolveJS(source.getIconFieldId()));
        listControl.setBadgeFieldId(p.resolveJS(source.getBadgeFieldId()));
        listControl.setBadgeColorFieldId(p.resolveJS(source.getBadgeColorFieldId()));
        listControl.setImageFieldId(p.resolveJS(source.getImageFieldId()));
        listControl.setGroupFieldId(p.resolveJS(source.getGroupFieldId()));
        if (source.getQueryId() != null)
            initDataProvider(listControl, source, p);
        else if (source.getOptions() != null) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (Map<String, String> option : source.getOptions()) {
                DataSet dataItem = new DataSet();
                option.forEach((f, v) -> dataItem.put(f, p.resolve(v)));
                list.add(dataItem);
            }
            listControl.setData(list);
        }
        listControl.setValueFieldId(p.cast(p.resolveJS(listControl.getValueFieldId()), "id"));
        listControl.setLabelFieldId(p.cast(p.resolveJS(listControl.getLabelFieldId()), "name"));
        listControl.setCaching(source.getCache());
        listControl.setHasSearch(p.cast(source.getSearch(), false));
        return compileStandardField(listControl, source, context, p);
    }

    @Override
    protected Object compileDefValues(S source, CompileProcessor p) {
        if (source.getDefValue() == null) {
            return null;
        }
        DefaultValues values = new DefaultValues();
        values.setValues(new HashMap<>());
        source.getDefValue().forEach((f, v) -> values.getValues().put(f, p.resolve(v)));
        return values;
    }

    private void initDataProvider(T listControl, N2oListField source, CompileProcessor p) {
        WidgetDataProvider dataProvider = new WidgetDataProvider();
        QueryContext queryContext = new QueryContext(source.getQueryId());
        ModelsScope modelsScope = p.getScope(ModelsScope.class);
        queryContext.setFailAlertWidgetId(modelsScope != null ? modelsScope.getWidgetId() : null);
        CompiledQuery query = p.getCompiled(queryContext);
        String route = query.getRoute();
        p.addRoute(route, queryContext);
        dataProvider.setUrl(p.resolveText(property("n2o.config.data.route")) + route);

        String searchFilterId = p.cast(source.getSearchFieldId(), source.getLabelFieldId());
        if (query.getFilterIdToParamMap().containsKey(searchFilterId)) {
            dataProvider.setQuickSearchParam(query.getFilterIdToParamMap().get(searchFilterId));
        }

        N2oPreFilter[] preFilters = source.getPreFilters();
        Map<String, BindLink> queryMap = new StrictMap<>();
        if (preFilters != null) {
            for (N2oPreFilter preFilter : preFilters) {
                N2oQuery.Filter filter = query.getFilterByPreFilter(preFilter);
                String filterParam = query.getFilterIdToParamMap().get(filter.getFilterField());
                Object prefilterValue = getPrefilterValue(preFilter);
                if (StringUtils.isJs(prefilterValue) && modelsScope != null) {
                    ModelLink link = new ModelLink(modelsScope.getModel(), modelsScope.getWidgetId(), null, filterParam);
                    link.setValue(prefilterValue);
                    queryMap.put(filterParam, link);
                } else {
                    queryMap.put(filterParam, new ModelLink(prefilterValue));
                }
            }
        }
        dataProvider.setQueryMapping(queryMap);
        listControl.setDataProvider(dataProvider);
    }

    private Object getPrefilterValue(N2oPreFilter n2oPreFilter) {
        if (n2oPreFilter.getValues() == null) {
            return ScriptProcessor.resolveExpression(n2oPreFilter.getValue());
        } else {
            return ScriptProcessor.resolveArrayExpression(n2oPreFilter.getValues());
        }
    }
}
