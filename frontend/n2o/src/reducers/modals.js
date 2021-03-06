import _ from 'lodash';
import { INSERT, DESTROY, HIDE, SHOW } from '../constants/modals';

const defaultState = {
  visible: false,
  name: null,
  props: {
    title: null,
    closeButton: null,
    pageId: null,
    size: 'lg',
    src: null
  }
};

function resolve(state = defaultState, action) {
  switch (action.type) {
    case INSERT:
      const { visible, name, ...props } = action.payload;
      return Object.assign({}, state, {
        visible,
        name,
        props: Object.assign({}, props)
      });
    case SHOW:
      return Object.assign({}, state, {
        visible: true
      });
    case HIDE:
      return Object.assign({}, state, {
        visible: false
      });
    default:
      return state;
  }
}

/**
 * Редюсер экшенов модалок
 */
export default function modals(state = [], action) {
  const index = state.findIndex(modal => modal.name === action.name);
  switch (action.type) {
    case INSERT:
      return [...state, resolve({}, action)];
    case SHOW:
      if (index >= 0) {
        state[index].visible = true;
        return state.slice();
      }
      return state;
    case HIDE:
      if (index >= 0) {
        state[index].visible = false;
        return state.slice();
      }
      return state;
    case DESTROY:
      return state.slice(0, -1);
    default:
      return state;
  }
}
