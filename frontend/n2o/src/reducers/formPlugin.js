import _ from 'lodash';
import merge from 'deepmerge';
import {
  DISABLE_FIELD,
  ENABLE_FIELD,
  SHOW_FIELD,
  HIDE_FIELD,
  REGISTER_FIELD_EXTRA,
  ADD_FIELD_MESSAGE,
  REMOVE_FIELD_MESSAGE,
  REGISTER_DEPENDENCY,
  SET_FIELD_FILTER,
  SHOW_FIELDS,
  HIDE_FIELDS,
  ENABLE_FIELDS,
  DISABLE_FIELDS
} from '../constants/formPlugin';

const defaultState = {
  isInit: true,
  visible: true,
  disabled: false,
  message: null,
  filter: []
};

const setValueByNames = (state, names, props) =>
  Object.assign(
    {},
    state,
    _.reduce(
      names,
      (result, name) => {
        return {
          ...result,
          [name]: { ...state[name], ...props }
        };
      },
      {}
    )
  );

function resolve(state = defaultState, action) {
  switch (action.type) {
    case REGISTER_FIELD_EXTRA:
      return Object.assign({}, state, merge(defaultState, action.payload.initialState || {}));
    case DISABLE_FIELD:
      return Object.assign({}, state, { disabled: true });
    case ENABLE_FIELD:
      return Object.assign({}, state, { disabled: false });
    case SHOW_FIELD:
      return Object.assign({}, state, { visible: true });
    case HIDE_FIELD:
      return Object.assign({}, state, { visible: false });
    case ADD_FIELD_MESSAGE:
      return Object.assign({}, state, { message: { ...action.payload.message } });
    case REMOVE_FIELD_MESSAGE:
      return Object.assign({}, state, { message: null });
    case REGISTER_DEPENDENCY:
      return Object.assign({}, state, { dependency: action.payload.dependency });
    case SET_FIELD_FILTER:
      return Object.assign({}, state, {
        filter: state.filter
          .filter(f => f.filterId !== action.payload.filter.filterId)
          .concat(action.payload.filter)
      });
    case SHOW_FIELDS:
      return setValueByNames(state, action.payload.names, { visible: true });
    case DISABLE_FIELDS:
      return setValueByNames(state, action.payload.names, { disabled: true });
    case ENABLE_FIELDS:
      return setValueByNames(state, action.payload.names, { disabled: false });
    case HIDE_FIELDS:
      return setValueByNames(state, action.payload.names, { visible: false });
    default:
      return state;
  }
}

/**
 * Редюсер удаления/добваления алертов
 * @ignore
 */
export default function formPlugin(state = {}, action) {
  switch (action.type) {
    case REGISTER_FIELD_EXTRA:
    case DISABLE_FIELD:
    case ENABLE_FIELD:
    case SHOW_FIELD:
    case HIDE_FIELD:
    case ADD_FIELD_MESSAGE:
    case REMOVE_FIELD_MESSAGE:
    case REGISTER_DEPENDENCY:
    case SET_FIELD_FILTER:
      return _.set(
        state,
        ['registeredFields', action.payload.name],
        resolve(_.get(state, ['registeredFields', action.payload.name]), action)
      );
    case SHOW_FIELDS:
    case DISABLE_FIELDS:
    case ENABLE_FIELDS:
    case HIDE_FIELDS:
      return _.set(state, 'registeredFields', resolve(_.get(state, 'registeredFields'), action));
    default:
      return state;
  }
}
