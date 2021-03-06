import React from 'react';
import { storiesOf } from '@storybook/react';
import { withKnobs, text, boolean, select } from '@storybook/addon-knobs/react';

import SideBar from './SideBar';
import SidebarContainer from './SidebarContainer';
import AuthButtonContainer from '../../core/auth/AuthLogin';
import Template from '../OLD_SidebarFixTemplate';
import Wireframe from '../../components/snippets/Wireframe/Wireframe';
import sidebarMetadata from './sidebarMetadata.meta.json';

const stories = storiesOf('UI Компоненты/Меню слева', module);

stories.addDecorator(withKnobs);

stories
  .addWithJSX('Компонент', () => {
    const props = {
      brandImage: text('brandImage', 'https://avatars0.githubusercontent.com/u/25926683?s=200&v=4'),
      color: select('color', ['default', 'inverse'], 'inverse'),
      fixed: boolean('fixed', false),
      activeId: text('activeId', 'link'),
      collapse: boolean('collapse', false),
      className: text('className', 'n2o'),
      search: boolean('search', false),
      visible: boolean('visible', true),
      items: sidebarMetadata.items
    };
    return (
      <Template>
        <SideBar {...props} />
        <div
          style={{
            width: '100%',
            position: 'relative'
          }}
        >
          <Wireframe style={{ top: 0, bottom: 0 }} className="n2o" title="Тело страницы" />
        </div>
      </Template>
    );
  })
  .addWithJSX('Ограничение доступа', () => {
    return (
      <Template>
        <SidebarContainer {...sidebarMetadata} />
        <div style={{ width: '100%', position: 'relative' }}>
          <small>
            Введите <mark>admin</mark>, чтобы увидеть скрытый элемент меню
          </small>
          <AuthButtonContainer />
          <br />
        </div>
      </Template>
    );
  });
