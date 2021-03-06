package net.n2oapp.framework.config.metadata.compile.context;

import lombok.Getter;
import lombok.Setter;
import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.event.action.UploadType;
import net.n2oapp.framework.api.metadata.global.dao.N2oPreFilter;
import net.n2oapp.framework.api.metadata.global.view.action.control.Target;
import net.n2oapp.framework.api.metadata.global.view.page.N2oPage;
import net.n2oapp.framework.api.metadata.meta.Breadcrumb;
import net.n2oapp.framework.api.metadata.meta.Page;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class PageContext extends BaseCompileContext<Page, N2oPage> {

    private List<Breadcrumb> breadcrumbs;
    /**
     * Операция на кнопке отправки формы
     */
    private String submitOperationId;
    /**
     * Модель данных на кнопке отправки формы
     */
    private ReduxModel submitModel;
    /**
     * Заголовок кнопки отправки формы
     */
    private String submitLabel;
    /**
     * Главный виджет страницы
     */
    private String resultWidgetId;
    /**
     * Маршрут родителя
     */
    private String parentRoute;
    /**
     * Родительский виджет
     */
    private String parentWidgetId;
    /**
     * Наименование страницы
     */
    private String pageName;
    /**
     * Закрыть окно после успешной отправки формы
     */
    private Boolean closeOnSuccessSubmit;
    /**
     * Обновить данные виджета после успешной отправки формы
     */
    private Boolean refreshOnSuccessSubmit;
    /**
     * Обновить данные родительского виджета после закрытия страницы
     */
    private Boolean refreshOnClose;
    /**
     * Направить на URL адрес после успешной отправки формы
     */
    private String redirectUrlOnSuccessSubmit;
    /**
     * Сценарий перенаправления после успешной отправки формы
     */
    private Target redirectTargetOnSuccessSubmit;
    /**
     * Источник данных виджета при открытии страницы
     */
    private UploadType upload;
    /**
     * Список предустановленных фильтраций для основного виджета
     */
    private List<N2oPreFilter> preFilters;

    /**
     * Клиентский идентификатор страницы
     */
    private String clientPageId;

    public PageContext(String clientPageId) {
        super(clientPageId, N2oPage.class, Page.class);
    }

    public PageContext(String clientPageId, String route) {
        super(route, clientPageId, N2oPage.class, Page.class);
    }

    public PageContext(PageContext context, CompileProcessor p) {
        super(context, p);
        this.breadcrumbs = context.breadcrumbs;
        this.submitOperationId = context.submitOperationId;
        this.resultWidgetId = context.resultWidgetId;
        this.parentRoute = context.parentRoute;
        this.parentWidgetId = context.parentWidgetId;
        this.pageName = context.pageName;
        this.closeOnSuccessSubmit = context.closeOnSuccessSubmit;
        this.refreshOnSuccessSubmit = context.refreshOnSuccessSubmit;
        this.refreshOnClose = context.refreshOnClose;
        this.redirectUrlOnSuccessSubmit = context.redirectUrlOnSuccessSubmit;
        this.redirectTargetOnSuccessSubmit = context.redirectTargetOnSuccessSubmit;
        this.upload = context.upload;
        this.clientPageId = context.clientPageId;
    }

    public void setBreadcrumbs(List<Breadcrumb> breadcrumbs) {
        if (breadcrumbs != null)
            this.breadcrumbs = Collections.unmodifiableList(breadcrumbs);
        else
            this.breadcrumbs = null;
    }
}
