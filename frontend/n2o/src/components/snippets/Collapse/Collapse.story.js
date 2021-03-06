import React from 'react';
import { storiesOf } from '@storybook/react';
import { withKnobs, text, boolean, select } from '@storybook/addon-knobs/react';
import withTests from 'N2oStorybook/withTests';
import Collapse, { Panel } from './Collapse';

const stories = storiesOf('UI Компоненты/Collapse', module);

stories.addDecorator(withKnobs);
stories.addDecorator(withTests('Icon'));

const textToPanel =
  'Значимость этих проблем настолько очевидна, что сложившаяся структура организации требует определения и уточнения модели развития! Дорогие друзья, рамки и место обучения кадров создаёт предпосылки качественно новых шагов для дальнейших направлений развития проекта! Дорогие друзья, курс на социально-ориентированный национальный проект представляет собой интересный эксперимент проверки дальнейших направлений развития проекта.';

const CollapseToStory = props => (
  <Collapse defaultActiveKey="1">
    <Panel key="1" header="Первый" {...props}>
      {textToPanel}
    </Panel>
    <Panel key="2" header="Второй" {...props}>
      {textToPanel}
    </Panel>
    <Panel key="3" header="Третий" {...props}>
      {textToPanel}
    </Panel>
  </Collapse>
);

stories
  .add('Компонент type=default', () => {
    const props = {
      type: select('type', ['default', 'line', 'divider'], 'default'),
      activeKey: text('activeKey', ''),
      defaultActiveKey: text('defaultActiveKey', ''),
      destroyInactivePanel: boolean('destroyInactivePanel', false),
      accordion: boolean('accordion', false)
    };

    return <CollapseToStory {...props} />;
  })
  .add('Компонент type=line', () => {
    return <CollapseToStory type="line" />;
  })
  .add('Компонент type=divider', () => {
    return <CollapseToStory type="divider" />;
  });
