import {
  METADATA_REQUEST,
  METADATA_SUCCESS,
  METADATA_FAIL,
  MAP_URL,
  RESET
} from '../constants/pages';
import createActionHelper from './createActionHelper';

/**
 * Запрос за метаданными страницы
 * @param pageId - id страницы
 * @param rootPage - признак модального окна
 * @param pageUrl - url с возможностью плейсхолдерами из mapping
 * @param mapping - маппинг для плейсхолдеров pageUrl
 * @example
 * dispatch(metadataRequest("Page"))
 */
export function metadataRequest(pageId, rootPage, pageUrl, mapping) {
  return createActionHelper(METADATA_REQUEST)({ pageId, rootPage, pageUrl, mapping });
}

/**
 * Вспомогательный экшен. Успешный запрос за данными.
 * @ignore
 * @param pageId - уникальный идентификатор виджета
 * @param json - response в виде json
 */
export function metadataSuccess(pageId, json) {
  return createActionHelper(METADATA_SUCCESS)({ pageId, json });
}

/**
 * Вспомогательный экшен. Ошибка при запросе за данными.
 * @ignore
 * @param pageId - id страницы
 * @param err - ошибка
 */
export function metadataFail(pageId, err) {
  return createActionHelper(METADATA_FAIL)({ pageId, err });
}

export function mapUrl(pageId) {
  return createActionHelper(MAP_URL)({ pageId });
}

/**
 * сбросить состояние страницы в дефолтное положение
 * @param pageId
 */
export function resetPage(pageId) {
  return createActionHelper(RESET)({ pageId });
}
