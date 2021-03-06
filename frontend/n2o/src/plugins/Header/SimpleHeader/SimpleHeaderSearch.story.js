import React from 'react';
import { storiesOf } from '@storybook/react';

import simpleHeaderMetadata from './simpleHeaderData.json';
import SimpleHeader from './SimpleHeader';
import Wireframe from '../../../components/snippets/Wireframe/Wireframe';

const stories = storiesOf('UI Компоненты/Меню сверху', module);

stories.addWithJSX('Поиск', () => {
  return (
    <div>
      <SimpleHeader
        brandImage={'https://avatars0.githubusercontent.com/u/25926683?s=200&v=4'}
        activeId={'link'}
        fixed={false}
        search={true}
        items={simpleHeaderMetadata.items}
        extraItems={simpleHeaderMetadata.extraItems}
        color="inverse"
      />
      <div
        style={{
          padding: '200px',
          position: 'relative'
        }}
      >
        <Wireframe className="n2o" title="Тело страницы" />
      </div>
    </div>
  );
});

SimpleHeader.defaultProps = {
  color: 'default',
  fixed: true,
  collapsed: true,
  className: '',
  items: [],
  extraItems: [],
  search: false,
  style: {}
};
