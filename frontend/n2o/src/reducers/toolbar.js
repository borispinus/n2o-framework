import { omit, map, filter, mapValues } from 'lodash';
import {
  CHANGE_BUTTON_VISIBILITY,
  CHANGE_BUTTON_TITLE,
  CHANGE_BUTTON_COUNT,
  CHANGE_BUTTON_SIZE,
  CHANGE_BUTTON_COLOR,
  CHANGE_BUTTON_DISABLED,
  TOGGLE_BUTTON_DISABLED,
  TOGGLE_BUTTON_VISIBILITY,
  REGISTER_BUTTON,
  CHANGE_BUTTON_HINT,
  CHANGE_BUTTON_ICON,
  CHANGE_BUTTON_CLASS,
  CHANGE_BUTTON_STYLE
} from '../constants/toolbar';
import { RESET_STATE } from '../constants/widgets';
import { generateKey } from '../utils/id';

export const buttonState = {
  isInit: true,
  isVisible: true,
  count: 0,
  size: null,
  color: null,
  title: null,
  hint: null,
  icon: null,
  isDisabled: false,
  loading: false,
  error: null,
  conditions: null
};

function resolve(state = buttonState, action) {
  switch (action.type) {
    case CHANGE_BUTTON_VISIBILITY:
      return Object.assign({}, state, {
        isVisible: action.payload.visible
      });
    case TOGGLE_BUTTON_VISIBILITY:
      return Object.assign({}, state, {
        isVisible: !state.isVisible
      });
    case TOGGLE_BUTTON_DISABLED:
      return Object.assign({}, state, {
        isDisabled: !state.isDisabled
      });
    case CHANGE_BUTTON_TITLE:
      return Object.assign({}, state, {
        title: action.payload.title
      });
    case CHANGE_BUTTON_DISABLED:
      return Object.assign({}, state, {
        isDisabled: action.payload.disabled
      });
    case CHANGE_BUTTON_SIZE:
      return Object.assign({}, state, {
        size: action.payload.size
      });
    case CHANGE_BUTTON_COLOR:
      return Object.assign({}, state, {
        color: action.payload.color
      });
    case CHANGE_BUTTON_COUNT:
      return Object.assign({}, state, {
        count: action.payload.count
      });
    case CHANGE_BUTTON_HINT:
      return Object.assign({}, state, {
        hint: action.payload.hint
      });
    case CHANGE_BUTTON_ICON:
      return Object.assign({}, state, {
        icon: action.payload.icon
      });
    case CHANGE_BUTTON_CLASS:
      return Object.assign({}, state, {
        className: action.payload.className
      });
    case CHANGE_BUTTON_STYLE:
      return Object.assign({}, state, {
        style: action.payload.style
      });
    case RESET_STATE:
      return Object.assign({}, state, { isInit: false });
    default:
      return state;
  }
}

/**
 * Редюсер колонок
 * @ignore
 */
export default function toolbar(state = {}, action) {
  const { key, id: buttonId } = action.payload || {};
  switch (action.type) {
    case REGISTER_BUTTON:
      return Object.assign({}, state, {
        [key]: { ...state[key], [buttonId]: Object.assign({}, buttonState, action.payload) }
      });
    case CHANGE_BUTTON_VISIBILITY:
    case CHANGE_BUTTON_TITLE:
    case CHANGE_BUTTON_COUNT:
    case CHANGE_BUTTON_SIZE:
    case CHANGE_BUTTON_COLOR:
    case CHANGE_BUTTON_DISABLED:
    case TOGGLE_BUTTON_DISABLED:
    case TOGGLE_BUTTON_VISIBILITY:
    case CHANGE_BUTTON_HINT:
    case CHANGE_BUTTON_ICON:
    case CHANGE_BUTTON_STYLE:
    case CHANGE_BUTTON_CLASS:
      return Object.assign({}, state, {
        [key]: {
          ...state[key],
          [buttonId]: resolve(state[key][buttonId], action)
        }
      });
    case RESET_STATE:
      const { widgetId } = action.payload;
      return {
        ...state,
        [widgetId]: mapValues(state[widgetId], (button, buttonId) =>
          resolve(state[widgetId][buttonId], action)
        )
      };
    default:
      return state;
  }
}
