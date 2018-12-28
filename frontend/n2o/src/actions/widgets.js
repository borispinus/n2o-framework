import {
  REGISTER,
  DATA_REQUEST,
  DATA_SUCCESS,
  DATA_FAIL,
  RESOLVE,
  REMOVE,
  SHOW,
  HIDE,
  ENABLE,
  DISABLE,
  LOADING,
  UNLOADING,
  ALERT_ADD,
  ALERT_REMOVE,
  SORT_BY,
  CHANGE_COUNT,
  CHANGE_SIZE,
  CHANGE_PAGE,
  CHANGE_FILTERS_VISIBILITY,
  TOGGLE_FILTERS_VISIBILITY,
  RESET_STATE,
  SET_WIDGET_METADATA,
  SET_TABLE_SELECTED_ID,
  SET_ACTIVE
} from '../constants/widgets';
import { CHANGE_BUTTON_CLASS } from '../constants/toolbar';
import createActionHelper from './createActionHelper';

/**
 * Регистрация виджета в хранилище
 * @param widgetId - уникальный идентификатор виджета
 * @param initProps - стартовые свойства
 * @example
 * dispatch(registerWidget("Page.Widget", {isVisible: false}))
 */
export function registerWidget(widgetId, initProps) {
  return createActionHelper(REGISTER)({ widgetId, initProps });
}

/**
 * Запрос за данными
 * @param widgetId - уникальный идентификатор виджета
 * @param options - опции для `fetch`
 * @example
 * dispatch(dataRequestWidget("Page.Widget", {size: 200}))
 */
export function dataRequestWidget(widgetId, options = {}) {
  return createActionHelper(DATA_REQUEST)({ widgetId, options });
}

/**
 * Вспомогательный экшен. Успешный запрос за данными.
 * @ignore
 * @param widgetId - уникальный идентификатор виджета
 * @param json - response в виде json
 */
export function dataSuccessWidget(widgetId, json) {
  return createActionHelper(DATA_SUCCESS)(
    {
      widgetId,
      query: json
    },
    {
      ...json.meta
    }
  );
}

/**
 * Вспомогательный экшен. Ошибка при запросе за данными.
 * @ignore
 * @param widgetId - уникальный идентификатор виджета
 * @param err - объект ошибки
 * @param errorResponse - json ответа
 */
export function dataFailWidget(widgetId, err, errorResponse = {}) {
  return createActionHelper(DATA_FAIL)(
    {
      widgetId,
      err
    },
    {
      ...errorResponse.meta
    }
  );
}

/**
 * Вызывает действие разрешения виджета.
 * Side-effect: зависимости, простановка в resolve-модель
 * @param widgetId - уникальный идентификатор виджета
 */
export function resolveWidget(widgetId, resolveModel) {
  return createActionHelper(RESOLVE)({ widgetId, model: resolveModel });
}

/**
 * Удаляет виджет из хранилища
 * @param widgetId - уникальный идентификатор виджета
 * @example
 * dispatch(removeWidget("Page.Widget"))
 */
export function removeWidget(widgetId) {
  return createActionHelper(REMOVE)({ widgetId });
}

/**
 * Сделать виджет видимым
 * @param widgetId - уникальный идентификатор виджета
 * @example
 * dispatch(showWidget("Page.Widget"))
 */
export function showWidget(widgetId) {
  return createActionHelper(SHOW)({ widgetId });
}

/**
 * Сделать виджет невидимым
 * @param widgetId - уникальный идентификатор виджета
 * @example
 * dispatch(hideWidget("Page.Widget"))
 */
export function hideWidget(widgetId) {
  return createActionHelper(HIDE)({ widgetId });
}

/**
 * Сделать виджет разблокированым
 * @param widgetId - уникальный идентификатор виджета
 * @example
 * dispatch(enableWidget("Page.Widget"))
 */
export function enableWidget(widgetId) {
  return createActionHelper(ENABLE)({ widgetId });
}

/**
 * Сделать виджет заблокированым
 * @param widgetId - уникальный идентификатор виджета
 * @example
 * dispatch(disableWidget("Page.Widget"))
 */
export function disableWidget(widgetId) {
  return createActionHelper(DISABLE)({ widgetId });
}

/**
 * Активировать анимацию загрузки у виджета
 * @param widgetId - уникальный идентификатор виджета
 * @example
 * dispatch(loadingWidget("Page.Widget"))
 */
export function loadingWidget(widgetId) {
  return createActionHelper(LOADING)({ widgetId });
}

/**
 * Деактивировать анимацию загрузки у виджета
 * @param widgetId - уникальный идентификатор виджета
 * @example
 * dispatch(unloadingWidget("Page.Widget"))
 */
export function unloadingWidget(widgetId) {
  return createActionHelper(UNLOADING)({ widgetId });
}

/**
 * @ignore
 * @param widgetId
 * @param alertKey
 */
export function alertAddWidget(widgetId, alertKey) {
  return createActionHelper(ALERT_ADD)({ widgetId, alertKey });
}

/**
 * @ignore
 * @param widgetId
 * @param alertKey
 */
export function alertRemoveWidget(widgetId, alertKey) {
  return createActionHelper(ALERT_REMOVE)({ widgetId, alertKey });
}

/**
 * @ignore
 * @param widgetId
 * @param fieldKey
 * @param sortDirection
 */
export function sortByWidget(widgetId, fieldKey, sortDirection) {
  return createActionHelper(SORT_BY)({ widgetId, fieldKey, sortDirection });
}

/**
 * Меняет номер страницы виджета
 * Этот параметр используется при запросах на сервер
 * @param widgetId - уникальный идентификатор виджета
 * @param page - номер страницы
 */
export function changePageWidget(widgetId, page) {
  return createActionHelper(CHANGE_PAGE)({ widgetId, page });
}

/**
 * Меняет количество записей в виджете.
 * Этот параметр служит для отрисовки пейджинга
 * @param widgetId - уникальный идентификатор виджета
 * @param count - кол-во записей
 */
export function changeCountWidget(widgetId, count) {
  return createActionHelper(CHANGE_COUNT)({ widgetId, count });
}

/**
 * Меняет размер выборки виджета
 * Этот параметр используется при запросах на сервер
 * @param widgetId - уникальный идентификатор виджета
 * @param size - размер выборки
 */
export function changeSizeWidget(widgetId, size) {
  return createActionHelper(CHANGE_SIZE)({ widgetId, size });
}

/**
 * Показать фильтры виджета
 * @param widgetId
 */
export function showWidgetFilters(widgetId) {
  return changeFiltersVisibility(widgetId, true);
}

/**
 * Скрыть филтры виджета
 * @param widgetId
 */
export function hideWidgetFilters(widgetId) {
  return changeFiltersVisibility(widgetId, false);
}

/**
 * Изменить видимость фильтров виджета
 * @param widgetId
 * @param isFilterVisible
 * @returns {*}
 */
export function changeFiltersVisibility(widgetId, isFilterVisible) {
  return createActionHelper(CHANGE_FILTERS_VISIBILITY)({ widgetId, isFilterVisible });
}

/**
 * Изменить видимость фильтров виджета на противоположенную
 * @param widgetId
 */
export function toggleWidgetFilters(widgetId) {
  return createActionHelper(TOGGLE_FILTERS_VISIBILITY)({ widgetId });
}

export function resetWidgetState(widgetId) {
  return createActionHelper(RESET_STATE)({ widgetId });
}

export function setWidgetMetadata(pageId, widgetId, metadata) {
  return createActionHelper(SET_WIDGET_METADATA)({ pageId, widgetId, metadata });
}

/**
 * Изменить выбранную запись в виджете
 * @param widgetId
 * @param selectedId
 */
export function setTableSelectedId(widgetId, selectedId) {
  return createActionHelper(SET_TABLE_SELECTED_ID)({ widgetId, value: selectedId });
}

export function setActive(widgetId) {
  return createActionHelper(SET_ACTIVE)({ widgetId });
}
