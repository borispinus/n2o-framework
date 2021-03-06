import { createSelector } from 'reselect';

const columnsSelector = state => {
  return state.columns || {};
};

const getContainerColumns = key => createSelector(columnsSelector, columns => columns[key] || {});

const makeColumnByKeyAndIdSelector = (key, id) =>
  createSelector(getContainerColumns(key), columns => {
    return columns[id] || {};
  });

const isInitSelector = (key, id) =>
  createSelector(makeColumnByKeyAndIdSelector(key, id), column => {
    return column.isInit;
  });

const isVisibleSelector = (key, id) =>
  createSelector(makeColumnByKeyAndIdSelector(key, id), column => {
    return column.isVisible;
  });

const isDisabledSelector = (key, id) =>
  createSelector(makeColumnByKeyAndIdSelector(key, id), column => {
    return column.isDisabled;
  });

export { getContainerColumns, isVisibleSelector, isInitSelector, isDisabledSelector };
