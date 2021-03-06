package net.n2oapp.framework.config.metadata.compile.action;

import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.Source;
import net.n2oapp.framework.api.metadata.aware.ModelAware;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.event.action.N2oInvokeAction;
import net.n2oapp.framework.api.metadata.global.view.action.control.Target;
import net.n2oapp.framework.api.metadata.local.CompiledObject;
import net.n2oapp.framework.api.metadata.local.util.StrictMap;
import net.n2oapp.framework.api.metadata.meta.BindLink;
import net.n2oapp.framework.api.metadata.meta.action.invoke.InvokeAction;
import net.n2oapp.framework.api.metadata.meta.action.invoke.InvokeActionPayload;
import net.n2oapp.framework.api.metadata.meta.saga.AsyncMetaSaga;
import net.n2oapp.framework.api.metadata.meta.saga.MetaSaga;
import net.n2oapp.framework.api.metadata.meta.saga.RedirectSaga;
import net.n2oapp.framework.api.metadata.meta.saga.RefreshSaga;
import net.n2oapp.framework.api.metadata.meta.widget.RequestMethod;
import net.n2oapp.framework.api.metadata.meta.widget.WidgetDataProvider;
import net.n2oapp.framework.config.metadata.compile.ComponentScope;
import net.n2oapp.framework.config.metadata.compile.ParentRoteScope;
import net.n2oapp.framework.config.metadata.compile.ValidationList;
import net.n2oapp.framework.config.metadata.compile.context.ActionContext;
import net.n2oapp.framework.config.metadata.compile.context.ModalPageContext;
import net.n2oapp.framework.config.metadata.compile.context.PageContext;
import net.n2oapp.framework.config.metadata.compile.redux.Redux;
import net.n2oapp.framework.config.metadata.compile.widget.WidgetScope;
import org.springframework.stereotype.Component;

import java.util.Map;

import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.colon;
import static net.n2oapp.framework.api.metadata.compile.building.Placeholders.property;
import static net.n2oapp.framework.config.register.route.RouteUtil.absolute;
import static net.n2oapp.framework.config.register.route.RouteUtil.normalize;

/**
 * Сборка действия вызова операции
 */
@Component
public class InvokeActionCompiler extends AbstractActionCompiler<InvokeAction, N2oInvokeAction> {
    @Override
    public Class<? extends Source> getSourceClass() {
        return N2oInvokeAction.class;
    }

    @Override
    public InvokeAction compile(N2oInvokeAction source, CompileContext<?, ?> context, CompileProcessor p) {
        InvokeAction invokeAction = new InvokeAction();
        compileAction(invokeAction, source, p);
        invokeAction.setOperationId(source.getOperationId());
        invokeAction.getOptions().setType(p.resolve(property("n2o.api.action.invoke.type"), String.class));
        String targetWidgetId = initTargetWidget(source, context, p);
        ReduxModel targetWidgetModel = ReduxModel.RESOLVE;
        ComponentScope componentScope = p.getScope(ComponentScope.class);
        if (componentScope != null) {
            ModelAware modelAware = componentScope.unwrap(ModelAware.class);
            if (modelAware!= null && modelAware.getModel() != null) {
                targetWidgetModel = modelAware.getModel();
            }
        }
        String modalLink = Redux.createBindLink(targetWidgetId, targetWidgetModel).getBindLink();
        invokeAction.getOptions().getPayload().setModelLink(modalLink);
        invokeAction.getOptions().getMeta().setSuccess(initSuccessMeta(invokeAction, source, context, p, targetWidgetId));
        invokeAction.getOptions().getMeta().setFail(initFailMeta(invokeAction, source, context, p, targetWidgetId));
        invokeAction.getOptions().getPayload().setWidgetId(targetWidgetId);
        initDataProvider(invokeAction, source, context, p, targetWidgetModel);
        return invokeAction;
    }

    private MetaSaga initFailMeta(InvokeAction invokeAction, N2oInvokeAction source,
                                  CompileContext<?, ?> context, CompileProcessor p,
                                  String targetWidgetId) {
        MetaSaga metaSaga = new MetaSaga();
        metaSaga.setMessageWidgetId(targetWidgetId);
        return metaSaga;
    }


    private MetaSaga initSuccessMeta(InvokeAction invokeAction, N2oInvokeAction source,
                                   CompileContext<?, ?> context, CompileProcessor p, String targetWidgetId) {
        MetaSaga meta = new MetaSaga();
        boolean closeOnSuccess = p.cast(source.getCloseAfterSuccess(), false);
        boolean refresh = p.cast(source.getRefreshOnSuccess(), true);
        boolean redirect = source.getRedirectUrl() != null;
        String refreshWidgetId = targetWidgetId;
        if ((closeOnSuccess) && (context instanceof PageContext)) {
            refreshWidgetId = ((PageContext) context).getParentWidgetId();
        }
        meta.setMessageWidgetId(refreshWidgetId);
        if (closeOnSuccess) {
            if (context instanceof ModalPageContext)
                meta.setCloseLastModal(true);
            else if (!redirect) {
                String backRoute;
                if (context instanceof PageContext) {
                    backRoute = ((PageContext) context).getParentRoute();
                } else {
                    backRoute = "/";
                }
                meta.setRedirect(new RedirectSaga());
                meta.getRedirect().setPath(backRoute);
                meta.getRedirect().setTarget(Target.application);
            }
        }
        if (refresh) {
            meta.setRefresh(new RefreshSaga());
            meta.getRefresh().setType(RefreshSaga.Type.widget);
            meta.getRefresh().getOptions().setWidgetId(refreshWidgetId);
        }
        if (redirect) {
            if (context instanceof ModalPageContext)
                meta.setCloseLastModal(true);
            meta.setRedirect(new RedirectSaga());
            ParentRoteScope routeScope = p.getScope(ParentRoteScope.class);
            meta.getRedirect().setPath(absolute(source.getRedirectUrl(), routeScope != null ? routeScope.getUrl() : null));
            meta.getRedirect().setTarget(source.getRedirectTarget());
            meta.getRedirect().setServer(true);
        }
        return meta;
    }


    private void initDataProvider(InvokeAction invokeAction, N2oInvokeAction source,
                                  CompileContext<?, ?> context, CompileProcessor p,
                                  ReduxModel model) {
        InvokeActionPayload payload = invokeAction.getOptions().getPayload();
        AsyncMetaSaga metaSaga = invokeAction.getOptions().getMeta();
        WidgetDataProvider dataProvider = new WidgetDataProvider();
        Map<String, BindLink> pathMapping = new StrictMap<>();
        ParentRoteScope routeScope = p.getScope(ParentRoteScope.class);
        String path = p.cast(routeScope != null ? routeScope.getUrl() : null, context.getRoute(p), "");
        WidgetScope widgetScope = p.getScope(WidgetScope.class);
        if (widgetScope != null) {
            if (model.equals(ReduxModel.RESOLVE)) {
                String widgetSelectedId = widgetScope.getClientWidgetId() + "_id";
                path = path + normalize(colon(widgetSelectedId));
                pathMapping.put(widgetSelectedId, Redux.createBindLink(widgetScope.getClientWidgetId(), ReduxModel.RESOLVE, "id"));
            }
        }
        path = path + normalize(p.cast(source.getRoute(), source.getId()));
        dataProvider.setUrl(p.resolve(property("n2o.config.data.route"), String.class) + path);
        if (routeScope != null && routeScope.getPathMapping() != null) {
            for (String pathParam : routeScope.getPathMapping().keySet()) {
                BindLink bindLink = routeScope.getPathMapping().get(pathParam);
                pathMapping.put(pathParam, bindLink);
            }
        }
        dataProvider.setPathMapping(pathMapping);
        dataProvider.setMethod(RequestMethod.POST);
        payload.setDataProvider(dataProvider);
        CompiledObject compiledObject = p.getScope(CompiledObject.class);
        if (compiledObject == null)
            throw new N2oException("For compilation action [{0}] is necessary object!").addData(source.getId());
        invokeAction.setObjectId(compiledObject.getId());
        ValidationList validationList = p.getScope(ValidationList.class);
        ActionContext actionContext = new ActionContext(compiledObject.getId(), source.getOperationId(), path);
        actionContext.setValidations(validationList == null ? null : validationList.get(metaSaga.getFail().getMessageWidgetId(), model));
        actionContext.setRedirect(initServerRedirect(metaSaga));
        actionContext.setFailAlertWidgetId(metaSaga.getFail().getMessageWidgetId());
        actionContext.setMessagesForm(metaSaga.getFail().getMessageWidgetId());
        actionContext.setSuccessAlertWidgetId(metaSaga.getSuccess().getMessageWidgetId());
        p.addRoute(path, actionContext);
    }

    private RedirectSaga initServerRedirect(AsyncMetaSaga meta) {
        if (meta != null && meta.getSuccess() != null
                && meta.getSuccess().getRedirect() != null
                && meta.getSuccess().getRedirect().isServer())
            return meta.getSuccess().getRedirect();
        return null;
    }
}
