import React from 'react';
import { storiesOf } from '@storybook/react';

import withTests from 'N2oStorybook/withTests';
import Tabs from './Tabs';
import Tab from './Tab';
import Wireframe from '../../snippets/Wireframe/Wireframe';

const stories = storiesOf('Регионы/Вкладки', module);

stories.addDecorator(withTests('Tabs'));

stories
  .addWithJSX('Компонент', () => {
    return (
      <Tabs>
        <Tab id="one" title="Один" active>
          <div style={{ width: '100%', height: 400, position: 'relative' }}>
            <Wireframe title="Первый таб" />
          </div>
        </Tab>
        <Tab id="two" title="Два">
          <div style={{ width: '100%', height: 400, position: 'relative' }}>
            <Wireframe title="Второй таб" className="d-10" />
          </div>
        </Tab>
        <Tab id="three" title="Три">
          <div style={{ width: '100%', height: 400, position: 'relative' }}>
            <Wireframe title="Третий таб" className="l-10" />
          </div>
        </Tab>
      </Tabs>
    );
  })
  .addWithJSX('С отключенной вкладкой', () => {
    return (
      <Tabs>
        <Tab id="one" title="Один" active>
          <div style={{ width: '100%', height: 400, position: 'relative' }}>
            <Wireframe title="Первый таб" />
          </div>
        </Tab>
        <Tab id="two" title="Два(этот таб отключен)" disabled>
          <div style={{ width: '100%', height: 400, position: 'relative' }}>
            <Wireframe title="Второй таб" />
          </div>
        </Tab>
        <Tab id="three" title="Три">
          <div style={{ width: '100%', height: 400, position: 'relative' }}>
            <Wireframe title="Третий таб" />
          </div>
        </Tab>
      </Tabs>
    );
  })
  .addWithJSX('С иконками', () => {
    return (
      <Tabs>
        <Tab id="one" title="Google" icon="fa fa-google" active>
          <div style={{ width: '100%', height: 400, position: 'relative' }}>
            <Wireframe title="Первый таб" />
          </div>
        </Tab>
        <Tab id="two" title="Facebook" icon="fa fa-facebook">
          <div style={{ width: '100%', height: 400, position: 'relative' }}>
            <Wireframe title="Второй таб" />
          </div>
        </Tab>
        <Tab id="three" title="Amazon" icon="fa fa-amazon">
          <div style={{ width: '100%', height: 400, position: 'relative' }}>
            <Wireframe title="Третий таб" />
          </div>
        </Tab>
      </Tabs>
    );
  });
