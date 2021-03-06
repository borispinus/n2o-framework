import React from 'react';
import { mount } from 'enzyme';
import ButtonsCell from '../ButtonsCell';
import configureMockStore from 'redux-mock-store';
import { Provider } from 'react-redux';
import sinon from 'sinon';

const mockStore = configureMockStore();
const store = mockStore({});

const setup = (propOverrides = {}) => {
  const props = Object.assign({}, propOverrides);

  const wrapper = mount(
    <Provider store={store}>
      <ButtonsCell {...props} />
    </Provider>
  );

  return {
    props,
    wrapper
  };
};

describe('<ButtonsCell />', () => {
  it('Проверяет создание Кнопки', () => {
    const { wrapper } = setup({
      buttons: [{ label: 'test' }]
    });

    wrapper.update();
    expect(wrapper.find('HintButton').exists()).toBeTruthy();
  });

  it('Проверяет создание Dropdown', () => {
    const { wrapper } = setup({
      buttons: [
        {
          label: 'test',
          subMenu: [{ label: 'test' }]
        }
      ]
    });
    expect(wrapper.find('HintDropDown').exists()).toBeTruthy();
  });
  it('Проверяет отправку экшена', () => {
    const mockFn = sinon.spy();
    const { wrapper } = setup({
      callActionImpl: mockFn,
      buttons: [
        {
          label: 'test',
          action: 'test-action'
        }
      ]
    });

    wrapper.find('HintButton').simulate('click');
    expect(mockFn.calledOnce).toEqual(true);
    expect(mockFn.calledWith({ action: 'test-action' })).toEqual(true);
  });
});
