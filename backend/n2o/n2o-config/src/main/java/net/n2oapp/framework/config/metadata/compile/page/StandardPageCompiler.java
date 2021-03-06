package net.n2oapp.framework.config.metadata.compile.page;

import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.global.view.ActionsBar;
import net.n2oapp.framework.api.metadata.global.view.page.GenerateType;
import net.n2oapp.framework.api.metadata.global.view.page.N2oStandardPage;
import net.n2oapp.framework.api.metadata.global.view.region.N2oRegion;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.global.view.widget.toolbar.*;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.local.util.StrictMap;
import net.n2oapp.framework.api.metadata.meta.*;
import net.n2oapp.framework.api.metadata.meta.action.Action;
import net.n2oapp.framework.api.metadata.meta.region.Region;
import net.n2oapp.framework.api.metadata.meta.toolbar.Toolbar;
import net.n2oapp.framework.api.metadata.meta.widget.Widget;
import net.n2oapp.framework.config.metadata.compile.ComponentScope;
import net.n2oapp.framework.config.metadata.compile.IndexScope;
import net.n2oapp.framework.config.metadata.compile.ParentRoteScope;
import net.n2oapp.framework.config.metadata.compile.ValidationList;
import net.n2oapp.framework.config.metadata.compile.context.ModalPageContext;
import net.n2oapp.framework.config.metadata.compile.context.ObjectContext;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.compile.widget.MetaActions;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;
import net.n2oapp.framework.config.register.route.RouteUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;

@Component
public class StandardPageCompiler extends BasePageCompiler<N2oStandardPage> {


    @Override
    public Page compile(N2oStandardPage source, PageContext context, CompileProcessor p) {
        Page page = new Page();
        String pageRoute = initPageRoute(source, context, p);
        page.setId(p.cast(context.getClientPageId(), RouteUtil.convertPathToId(pageRoute)));
        PageScope pageScope = new PageScope();
        pageScope.setPageId(page.getId());
        String pageName = p.cast(context.getPageName(), source.getName());
        page.getProperties().setTitle(pageName);
        BreadcrumbList breadcrumb = initBreadcrumb(pageName, context, p);
        page.setBreadcrumb(breadcrumb);
        page.setWidgets(new StrictMap<>());
        Models models = new Models();
        page.setModels(models);
        //init base route
        PageRoutes pageRoutes = new PageRoutes();
        pageRoutes.addRoute(new PageRoutes.Route(pageRoute));
        List<N2oWidget> sourceWidgets = collectWidgets(source);
        initDefaults(context, sourceWidgets);
        ParentRoteScope routeScope = new ParentRoteScope(pageRoute, context.getPathRouteInfos());
        ValidationList validationList = new ValidationList(new HashMap<>());
        //compile widget
        page.setWidgets(initWidgets(routeScope, pageRoutes, sourceWidgets,
                context, p, pageScope, breadcrumb, validationList, models));
        registerRoutes(pageRoutes, context, p);
        if (!(context instanceof ModalPageContext))
            page.setRoutes(pageRoutes);
        //compile region
        page.setLayout(createLayout(source, p, context, pageScope));
        CompiledObject object = source.getObjectId() != null ? p.getCompiled(new ObjectContext(source.getObjectId())) : null;
        page.setObject(object);
        if (context.getSubmitOperationId() != null)
            initToolbarGenerate(source, context, p, sourceWidgets);
        MetaActions metaActions = new MetaActions();
        compileToolbarAndAction(page, source, context, p, metaActions, pageScope, routeScope, object, breadcrumb, validationList);
        page.setActions(metaActions);
        return page;
    }

    private void initDefaults(PageContext context, List<N2oWidget> sourceWidgets) {
        if ((context.getPreFilters() != null && !context.getPreFilters().isEmpty()) || (context.getUpload() != null)) {
            N2oWidget widget = initResultWidget(context, sourceWidgets);
            widget.addPreFilters(context.getPreFilters());
            widget.setUpload(context.getUpload());
        }
    }

    private String initResultWidgetId(PageContext context, List<N2oWidget> sourceWidgets) {
        String resultWidgetId = context.getResultWidgetId();
        if (resultWidgetId == null) {
            List<N2oWidget> sourceIndependents = getSourceIndependents(sourceWidgets);
            if (sourceIndependents.size() == 1)
                resultWidgetId = sourceIndependents.get(0).getId();//todo может быть не задан id в виджете
            else
                throw new N2oException("Can't get result widget id. There were two independent's widgets");
        }
        return resultWidgetId;
    }

    private N2oWidget initResultWidget(PageContext context, List<N2oWidget> sourceWidgets) {
        String resultWidgetId = context.getResultWidgetId();
        if (resultWidgetId != null) {
            for (N2oWidget sourceWidget : sourceWidgets) {
                if (sourceWidget.getId() != null && sourceWidget.getId().equals(resultWidgetId))
                    return sourceWidget;
            }
            throw new N2oException("Widget " + resultWidgetId + " not found!");
        } else {
            List<N2oWidget> sourceIndependents = getSourceIndependents(sourceWidgets);
            if (sourceIndependents.size() == 1)
                return sourceIndependents.get(0);//todo может быть не задан id в виджете
            else
                throw new N2oException("Can't get result widget id. There were two independent's widgets");
        }
    }

    private void compileToolbarAndAction(Page compiled, N2oStandardPage source, PageContext context, CompileProcessor p,
                                         MetaActions metaActions, PageScope pageScope, ParentRoteScope routeScope,
                                         CompiledObject object, BreadcrumbList breadcrumbs, ValidationList validationList) {
        actionsToToolbar(source);
        compiled.setToolbar(compileToolbar(source, context, p, metaActions, pageScope, routeScope, object, breadcrumbs, validationList));
        compileActions(source, context, p, metaActions, pageScope, routeScope, object, breadcrumbs, validationList);
    }


    private void actionsToToolbar(N2oStandardPage source) {
        if (source.getActions() == null || source.getToolbars() == null)
            return;
        Map<String, ActionsBar> actionMap = new HashMap<>();
        Stream.of(source.getActions()).forEach(a -> actionMap.put(a.getId(), a));
        for (N2oToolbar toolbar : source.getToolbars()) {
            if (toolbar.getItems() == null) continue;
            ToolbarItem[] toolbarItems = toolbar.getItems();
            copyActionForToolbarItem(actionMap, toolbarItems);
        }
    }

    private void copyActionForToolbarItem(Map<String, ActionsBar> actionMap, ToolbarItem[] toolbarItems) {
        for (ToolbarItem item : toolbarItems) {
            if (item instanceof N2oButton || item instanceof N2oMenuItem) {
                copyAction((AbstractMenuItem) item, actionMap);
            } else if (item instanceof N2oSubmenu) {
                for (N2oMenuItem subItem : ((N2oSubmenu) item).getMenuItems()) {
                    copyAction(subItem, actionMap);
                }
            } else if (item instanceof N2oGroup) {
                copyActionForToolbarItem(actionMap, ((N2oGroup) item).getItems());
            }
        }
    }

    private void copyAction(AbstractMenuItem item, Map<String, ActionsBar> actionMap) {
        AbstractMenuItem mi = item;
        if (mi.getAction() == null && mi.getActionId() != null) {
            ActionsBar action = actionMap.get(mi.getActionId());
            if (action == null) {
                throw new N2oException("Toolbar has reference to nonexistent action by actionId {0}!").addData(mi.getActionId());
            }
            mi.setAction(action.getAction());
            if (mi.getModel() == null)
                mi.setModel(action.getModel());
            if (mi.getWidgetId() == null)
                mi.setWidgetId(action.getWidgetId());
            if (mi.getLabel() == null)
                mi.setLabel(action.getLabel());
            if (mi.getIcon() == null)
                mi.setIcon(action.getIcon());
        }
    }

    private Toolbar compileToolbar(N2oStandardPage source, PageContext context, CompileProcessor p,
                                   MetaActions metaActions, PageScope pageScope, ParentRoteScope routeScope,
                                   CompiledObject object, BreadcrumbList breadcrumbs, ValidationList validationList) {
        if (source.getToolbars() == null)
            return null;
        Toolbar toolbar = new Toolbar();
        for (N2oToolbar n2oToolbar : source.getToolbars()) {
            toolbar.putAll(p.compile(n2oToolbar, context, metaActions, pageScope, routeScope, object, breadcrumbs, validationList, new IndexScope()));
        }
        return toolbar;
    }

    private Map<String, Widget> initWidgets(ParentRoteScope routeScope, PageRoutes pageRoutes, List<N2oWidget> sourceWidgets,
                                            PageContext context, CompileProcessor p,
                                            PageScope pageScope, BreadcrumbList breadcrumbs, ValidationList validationList,
                                            Models models) {
        Map<String, Widget> compiledWidgets = new StrictMap<>();
        IndexScope indexScope = new IndexScope();
        getSourceIndependents(sourceWidgets).forEach(w -> compileWidget(w, pageRoutes, routeScope, null,
                sourceWidgets, compiledWidgets,
                context, p,
                pageScope, breadcrumbs, validationList, models, indexScope));
        return compiledWidgets;
    }

    private void compileWidget(N2oWidget w,
                               PageRoutes routes,
                               ParentRoteScope parentRoute,
                               String parentWidgetId,
                               List<N2oWidget> sourceWidgets,
                               Map<String, Widget> compiledWidgets,
                               PageContext context, CompileProcessor p,
                               PageScope pageScope, BreadcrumbList breadcrumbs, ValidationList validationList,
                               Models models, IndexScope indexScope) {
        WidgetScope widgetScope = new WidgetScope();
        widgetScope.setDependsOnWidgetId(parentWidgetId);
        ParentRoteScope routeScope = new ParentRoteScope(parentRoute.getUrl(), parentRoute.getPathMapping());
        Widget compiledWidget = p.compile(w, context, indexScope, routes, pageScope, widgetScope, routeScope,
                breadcrumbs, validationList, models);
        compiledWidgets.put(compiledWidget.getId(), compiledWidget);
        //master/detail filter
        if (compiledWidget.getSelectedRoute() != null) {
            Map<String, ModelLink> pathMappings = new HashMap<>(parentRoute.getPathMapping());
            pathMappings.putAll(compiledWidget.getPathMapping());
            ParentRoteScope parentRouteScope = new ParentRoteScope(compiledWidget.getSelectedRoute(), pathMappings);
            //compile detail widgets
            getDetails(w.getId(), sourceWidgets).forEach(detWgt -> {
                compileWidget(detWgt, routes, parentRouteScope, compiledWidget.getId(),
                        sourceWidgets, compiledWidgets,
                        context, p,
                        pageScope, breadcrumbs, validationList, models, indexScope);
            });
        }
    }

    private Layout createLayout(N2oStandardPage source, CompileProcessor p, PageContext context, PageScope pageScope) {
        Layout layout = new Layout();
        layout.setSrc(p.cast(source.getLayout(), p.resolve(property("n2o.api.page.layout.src"), String.class)));
        Map<String, List<Region>> regionMap = new HashMap<>();
        if (source.getN2oRegions() != null) {
            IndexScope index = new IndexScope();
            for (N2oRegion n2oRegion : source.getN2oRegions()) {
                Region region = p.compile(n2oRegion, context, index, pageScope);
                String place = p.cast(n2oRegion.getPlace(), "single");
                if (regionMap.get(place) != null) {
                    regionMap.get(place).add(region);
                } else {
                    List<Region> regionList = new ArrayList<>();
                    regionList.add(region);
                    regionMap.put(place, regionList);
                }
            }
            layout.setRegions(regionMap);
        }
        return layout;
    }

    @Override
    public Class<N2oStandardPage> getSourceClass() {
        return N2oStandardPage.class;
    }

    private List<N2oWidget> collectWidgets(N2oStandardPage page) {
        List<N2oWidget> result = new ArrayList<>();
        if (page.getN2oRegions() != null) {
            for (N2oRegion region : page.getN2oRegions()) {
                if (region.getWidgets() != null) {
                    result.addAll(Arrays.asList(region.getWidgets()));
                }
            }
        }
        return result;
    }

    private void initToolbarGenerate(N2oStandardPage source, PageContext context, CompileProcessor p, List<N2oWidget> sourceWidgets) {
        N2oToolbar n2oToolbar = new N2oToolbar();
        String[] generate = new String[]{GenerateType.submit.name(), GenerateType.close.name()};
        n2oToolbar.setGenerate(generate);
        String resultWidgetId = initResultWidgetId(context, sourceWidgets);
        n2oToolbar.setTargetWidgetId(resultWidgetId);
        if (source.getToolbars() == null) {
            source.setToolbars(new N2oToolbar[0]);
        }
        int length = source.getToolbars().length;
        N2oToolbar[] n2oToolbars = new N2oToolbar[length + 1];
        System.arraycopy(source.getToolbars(), 0, n2oToolbars, 0, length);
        n2oToolbars[length] = n2oToolbar;
        source.setToolbars(n2oToolbars);
    }

    private List<N2oWidget> getSourceIndependents(List<N2oWidget> sourceWidgets) {
        List<N2oWidget> independents = new ArrayList<>();
        for (N2oWidget widget : sourceWidgets) {
            if (widget.getDependsOn() == null)
                independents.add(widget);
        }
        return independents;
    }

    private List<N2oWidget> getDetails(String widgetId, List<N2oWidget> widgets) {
        List<N2oWidget> details = new ArrayList<>();
        for (N2oWidget widget : widgets) {
            if (widget.getDependsOn() != null && widget.getDependsOn().equals(widgetId))
                details.add(widget);
        }
        return details;
    }

    private void compileActions(N2oStandardPage source, PageContext context, CompileProcessor p,
                                MetaActions actions, PageScope pageScope, ParentRoteScope routeScope, CompiledObject object,
                                BreadcrumbList breadcrumbs, ValidationList validationList) {
        if (source.getActions() != null) {
            Stream.of(source.getActions()).forEach(a -> {
                a.getAction().setId(a.getId());
                Action action = p.compile(a.getAction(), context, pageScope, routeScope, object, breadcrumbs, validationList, new ComponentScope(a));
                actions.addAction(action);
            });
        }
    }
}