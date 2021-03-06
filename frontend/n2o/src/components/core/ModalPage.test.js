import React from 'react';
import { ModalWindow } from './ModalPage';
import { createStore } from 'redux';
import { Provider } from 'react-redux';
import rootReducer from '../../reducers';
function configureStore() {
  return createStore(rootReducer);
}
const store = configureStore();
const setup = (propOverrides, storeOverrides) => {
  const props = {
    pageUrl: '/modalPage',
    pageId: 'modalPage',
    visible: true,
    close: false,
    pages: {}
  };
  return mount(
    <Provider store={{ ...store, ...storeOverrides }}>
      <ModalWindow {...props} {...propOverrides} />
    </Provider>
  );
};
describe('Тесты ModalPage', function() {
  it('CoverSpinner должен рендериться, если metadata пуста', () => {
    const wrapper = setup();
    expect(wrapper.find('.spinner-container').exists()).toBeTruthy();
  });
  it('CoverSpinner не должен рендериться, если metadata не пуста', () => {
    const wrapper = setup({
      pageUrl: '/modalPage',
      pageId: 'modalPage',
      close: false,
      pages: {},
      loading: false
    });

    expect(wrapper.find('.spinner-container').exists()).toBeFalsy();
  });
});
