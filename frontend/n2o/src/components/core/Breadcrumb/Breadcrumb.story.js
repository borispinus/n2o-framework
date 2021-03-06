import React from 'react';
import { set } from 'lodash';
import { Route, Link, Switch } from 'react-router-dom';
import fetchMock from 'fetch-mock';
import { storiesOf } from '@storybook/react';
import { withKnobs, text, boolean, object } from '@storybook/addon-knobs/react';
import withTests from 'N2oStorybook/withTests';
import { getStubData } from 'N2oStorybook/fetchMock';

import metadata from '../Page.meta';
import Page from '../Page';

const stories = storiesOf('Функциональность/Хлебные крошки', module);

stories.addDecorator(withKnobs);
stories.addDecorator(withTests('Page'));

stories.add('Метаданные', () => {
  const withForward = JSON.parse(JSON.stringify(metadata));
  withForward.id = 'OtherPage';
  withForward.widgets['Page_Wireframe'].toolbar.topLeft[0].buttons[0].title = 'Назад';
  withForward.widgets['Page_Wireframe'].toolbar.topLeft[0].buttons[0].id = 'back';
  withForward.widgets['Page_Wireframe'].actions.redirect.options.dataProvider = {
    url: '/',
    pathMapping: {},
    queryMapping: {}
  };
  withForward.widgets['Page_Wireframe'].wireframe.title = 'Виджет второй страницы';
  withForward.widgets['Page_Wireframe'].wireframe.className = 'd-10';

  fetchMock.restore().get('begin:n2o/page', url => {
    if (url === 'n2o/page/Page') {
      return { ...metadata, breadcrumb: metadata.breadcrumb.slice(0, 1) };
    }
    return withForward;
  });

  fetchMock.get('begin:n2o/data', getStubData);

  return (
    <Switch>
      <Route path="/test" exact component={() => <Page pageId="OtherPage" pageUrl="OtherPage" />} />
      <Route path="/" component={() => <Page pageId="testSimplePageJson" pageUrl="Page" />} />
    </Switch>
  );
});
