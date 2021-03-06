import React from 'react';
import sinon from 'sinon';

import Checkbox from '../Checkbox/Checkbox';
import CheckboxGroup from './CheckboxGroup';

const setup = (groupOverrides, checkboxOverrides) => {
  const groupProps = Object.assign(
    {
      // use this to assign some default props
    },
    groupOverrides
  );

  const checkboxProps = Object.assign(
    {
      // use this to assign some default props
    },
    checkboxOverrides
  );

  const wrapper = mount(
    <CheckboxGroup {...groupProps}>
      <Checkbox value="1" {...checkboxProps} />
      <Checkbox value="2" {...checkboxProps} />
    </CheckboxGroup>
  );

  return {
    groupProps,
    checkboxProps,
    wrapper
  };
};

describe('<CheckboxGroup />', () => {
  it('creates checkboxes ', () => {
    const { wrapper } = setup();
    expect(wrapper.find('input[type="checkbox"]')).toHaveLength(2);
  });

  it('sets properties properly', () => {
    const { wrapper } = setup({ name: 'name' });
    expect(wrapper.find(CheckboxGroup).props().name).toBe('name');
  });

  it('sets properties to input properly', () => {
    const { wrapper } = setup();
    expect(
      wrapper
        .find('input[type="checkbox"]')
        .first()
        .props().value
    ).toBe('1');
  });

  it('calls onChange', () => {
    const onChange = sinon.spy();
    const { wrapper } = setup({ onChange });
    wrapper
      .find('input[type="checkbox"]')
      .first()
      .simulate('change', { target: { checked: true } });
    expect(onChange.calledOnce).toBe(true);
  });

  it('проверяет inline', () => {
    const { wrapper } = setup({ inline: true });

    expect(wrapper.find('div.n2o-checkbox-inline').exists()).toBeTruthy();
  });
});
