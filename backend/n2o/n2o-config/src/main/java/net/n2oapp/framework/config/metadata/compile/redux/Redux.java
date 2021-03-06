package net.n2oapp.framework.config.metadata.compile.redux;

import net.n2oapp.framework.api.metadata.ReduxModel;
import net.n2oapp.framework.api.metadata.meta.BindLink;
import net.n2oapp.framework.api.metadata.meta.ModelLink;
import net.n2oapp.framework.api.metadata.meta.ReduxAction;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Взаимодействие c Redux моделями
 */
public abstract class Redux {

    /**
     * Создать ссылку на поле в Redux модели виджета
     *
     * @param widgetId Идентификатор виджета
     * @param model    Модель
     * @param field    Поле
     * @return Redux действие
     */
    @Deprecated
    public static BindLink createBindLink(String widgetId, ReduxModel model, String field) {
        //todo если поле genders*.id то нужно его превращать через js в массив и сетить в value
        return new ModelLink(model, widgetId, field);
    }

    /**
     * Создать ссылку на Redux модель виджета
     *
     * @param widgetId Идентификатор виджета
     * @param model    Модель
     * @return Redux действие
     */
    @Deprecated
    public static BindLink createBindLink(String widgetId, ReduxModel model) {
        return new ModelLink(model, widgetId);
    }

    /**
     * Создать ссылку на Redux состояние виджета
     *
     * @param widgetId   Идентификатор виджета
     * @param stateField Поле в состоянии виджета
     * @return Ссылка на Redux
     */
    public static BindLink createBindLink(String widgetId, String stateField) {
        return new BindLink(String.format("widgets['%s'].%s", widgetId, stateField));
    }

    /**
     * Создать ссылку на основе Redux действия
     *
     * @param reduxAction Идентификатор виджета
     * @return Redux действие
     */
    public static BindLink createBindLink(ReduxAction reduxAction) {
        ReduxModel reduxModel = null;
        if (reduxAction.getType().equals("n2o/widgets/CHANGE_SELECTED_ID")) {
            reduxModel = ReduxModel.RESOLVE;
            //todo нужна типизация по widgetId и field
            String widgetId = reduxAction.getPayload().get("widgetId").toString();
            return createBindLink(widgetId, reduxModel, "id");
        } else {
            if (reduxAction.getType().equals("n2o/models/UPDATE")){
                reduxModel = ReduxModel.valueOf(reduxAction.getPayload().get("prefix").toString().toUpperCase());
                String widgetId = reduxAction.getPayload().get("key").toString();
                String field = reduxAction.getPayload().get("field") == null ? null : reduxAction.getPayload().get("field").toString();
                return createBindLink(widgetId, reduxModel, field);
            } else {
                throw new UnsupportedOperationException("Redux action type " + reduxAction.getType() + " unsupported");
            }
        }

    }

    /**
     * Вызвать выделение записи в виджете
     *
     * @param widgetId Идентификатор виджета
     * @param value    Значение
     * @return Redux действие
     */
    public static ReduxAction dispatchSelectedWidget(String widgetId, Object value) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("widgetId", widgetId);
        params.put("value", value);
        return new ReduxAction("n2o/widgets/CHANGE_SELECTED_ID", params);
    }

    /**
     * Вызвать обновление поля в модели виджета
     *
     * @param widgetId Идентификатор виджета
     * @param model    Модель виджета
     * @param field    Поле виджета
     * @param value    Значение
     * @return Redux действие
     */
    public static ReduxAction dispatchUpdateModel(String widgetId, ReduxModel model, String field, Object value) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("prefix", model.getId());
        params.put("key", widgetId);
        params.put("field", field);
        params.put("value", value);
        return new ReduxAction("n2o/models/UPDATE", params);
    }

    /**
     * Вызвать обновление поля со списком в модели виджета
     *
     * @param widgetId Идентификатор виджета
     * @param model    Модель виджета
     * @param field    Поле виджета
     * @param map      Поле для маппинга
     * @param value    Значение
     * @return Redux действие
     */
    public static ReduxAction dispatchUpdateMapModel(String widgetId, ReduxModel model, String field, String map, Object value) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("prefix", model.getId());
        params.put("key", widgetId);
        params.put("field", field);
        params.put("value", value);
        params.put("map", map);
        return new ReduxAction("n2o/models/UPDATE_MAP", params);
    }

    /**
     * Вызвать сортировку виджета
     *
     * @param widgetId  Идентификатор виджета
     * @param field     Поле сортировки
     * @param direction Направление сортировки
     * @return Redux действие
     */
    public static ReduxAction dispatchSortWidget(String widgetId, String field, Object direction) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("widgetId", widgetId);
        params.put("fieldKey", field);
        params.put("sortDirection", direction);
        return new ReduxAction("n2o/widgets/SORT_BY", params);
    }
}
