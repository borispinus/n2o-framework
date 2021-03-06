import {
  CHANGE_LOCALE,
  REQUEST_CONFIG,
  REQUEST_CONFIG_SUCCESS,
  REQUEST_CONFIG_FAIL,
  CHANGE_ROOT_PAGE
} from '../constants/global';
import createActionHelper from './createActionHelper';

/**
 * Сменить язык интерфейса
 * @param locale
 */
export function changeLocale(locale) {
  return createActionHelper(CHANGE_LOCALE)({
    locale
  });
}

/**
 * Запрос за базовой настройкой приложения
 * @param params
 */
export function requestConfig(params) {
  return createActionHelper(REQUEST_CONFIG)({
    params
  });
}

/**
 * Успешный вызов настройки приложения
 * @param config
 */
export function requestConfigSuccess(config) {
  return createActionHelper(REQUEST_CONFIG_SUCCESS)({
    config
  });
}

/**
 * Ошибки при запросе за настройкой приложения
 * @param error
 */

export function requestConfigFail(error) {
  return createActionHelper(REQUEST_CONFIG_FAIL)({
    error
  });
}

/**
 * Сменить текущую страницу
 * @param rootPageId
 */
export function changeRootPage(rootPageId) {
  return createActionHelper(CHANGE_ROOT_PAGE)({
    rootPageId
  });
}
