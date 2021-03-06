import React from 'react';
import { storiesOf } from '@storybook/react';
import { withKnobs, text, boolean, number } from '@storybook/addon-knobs/react';
import withTests from 'N2oStorybook/withTests';
import { withState } from '@dump247/storybook-state';

import RadioN2O from './RadioN2O';

const stories = storiesOf('Контролы/Радио', module);

stories.addDecorator(withKnobs);
stories.addDecorator(withTests('Checkbox'));

stories.add(
  'N2O радио',
  withState({ checked: false }, store => {
    const props = {
      value: number('value', 2),
      disabled: boolean('disabled', false),
      checked: boolean('checked', store.state.checked),
      label: text('label', 'Label')
    };

    return <RadioN2O {...props} onChange={() => store.set({ checked: !store.state.checked })} />;
  })
);
