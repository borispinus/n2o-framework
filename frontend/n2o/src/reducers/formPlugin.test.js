import {
  DISABLE_FIELD,
  ENABLE_FIELD,
  SHOW_FIELD,
  HIDE_FIELD,
  REGISTER_FIELD_EXTRA,
  ADD_FIELD_MESSAGE,
  REMOVE_FIELD_MESSAGE,
  REGISTER_DEPENDENCY,
  SET_FIELD_FILTER
} from '../constants/formPlugin';
import formPlugin from './formPlugin';

describe('Тесты formPlugin reducer', () => {
  it('Проверка DISABLE_FIELD', () => {
    expect(
      formPlugin(
        {},
        {
          type: DISABLE_FIELD,
          payload: {
            name: 'testName'
          }
        }
      )
    ).toEqual({
      registeredFields: {
        testName: {
          disabled: true,
          filter: [],
          isInit: true,
          message: null,
          visible: true
        }
      }
    });
  });

  it('Проверка ENABLE_FIELD', () => {
    expect(
      formPlugin(
        {},
        {
          type: ENABLE_FIELD,
          payload: {
            name: 'testName'
          }
        }
      )
    ).toEqual({
      registeredFields: {
        testName: {
          disabled: false,
          filter: [],
          isInit: true,
          message: null,
          visible: true
        }
      }
    });
  });

  it('Проверка SHOW_FIELD', () => {
    expect(
      formPlugin(
        {},
        {
          type: SHOW_FIELD,
          payload: {
            name: 'testName'
          }
        }
      )
    ).toEqual({
      registeredFields: {
        testName: {
          disabled: false,
          filter: [],
          isInit: true,
          message: null,
          visible: true
        }
      }
    });
  });

  it('Проверка HIDE_FIELD', () => {
    expect(
      formPlugin(
        {},
        {
          type: HIDE_FIELD,
          payload: {
            name: 'testName'
          }
        }
      )
    ).toEqual({
      registeredFields: {
        testName: {
          disabled: false,
          filter: [],
          isInit: true,
          message: null,
          visible: false
        }
      }
    });
  });

  it('Проверка ADD_FIELD_MESSAGE', () => {
    expect(
      formPlugin(
        {},
        {
          type: ADD_FIELD_MESSAGE,
          payload: {
            message: ['message'],
            name: 'testName'
          }
        }
      )
    ).toEqual({
      registeredFields: {
        testName: {
          disabled: false,
          filter: [],
          isInit: true,
          message: {
            0: 'message'
          },
          visible: true
        }
      }
    });
  });

  it('Проверка REMOVE_FIELD_MESSAGE', () => {
    expect(
      formPlugin(
        {},
        {
          type: REMOVE_FIELD_MESSAGE,
          payload: {
            message: ['message'],
            name: 'testName'
          }
        }
      )
    ).toEqual({
      registeredFields: {
        testName: {
          disabled: false,
          filter: [],
          isInit: true,
          message: null,
          visible: true
        }
      }
    });
  });

  it('Проверка REGISTER_FIELD_EXTRA', () => {
    expect(
      formPlugin(
        {},
        {
          type: REGISTER_FIELD_EXTRA,
          payload: {
            name: 'testName',
            initialState: {
              visible: false,
              disabled: true
            }
          }
        }
      )
    ).toEqual({
      registeredFields: {
        testName: {
          disabled: true,
          filter: [],
          isInit: true,
          message: null,
          visible: false
        }
      }
    });
  });

  it('Проверка REGISTER_DEPENDENCY', () => {
    expect(
      formPlugin(
        {},
        {
          type: REGISTER_DEPENDENCY,
          payload: {
            name: 'testName',
            dependency: 'dependency'
          }
        }
      )
    ).toEqual({
      registeredFields: {
        testName: {
          disabled: false,
          filter: [],
          isInit: true,
          message: null,
          visible: true,
          dependency: 'dependency'
        }
      }
    });
  });

  it('Проверка SET_FIELD_FILTER', () => {
    expect(
      formPlugin(
        {},
        {
          type: SET_FIELD_FILTER,
          payload: {
            name: 'testName',
            filter: [
              {
                'filter.name': 'Oleg'
              }
            ]
          }
        }
      )
    ).toEqual({
      registeredFields: {
        testName: {
          disabled: false,
          isInit: true,
          message: null,
          visible: true,
          filter: [
            {
              'filter.name': 'Oleg'
            }
          ]
        }
      }
    });
  });
});
