import React from 'react';
import { storiesOf } from '@storybook/react';
import withTests from 'N2oStorybook/withTests';
import { set, pullAt, omit, pick } from 'lodash';

import TabsRegion from './TabsRegion';
import { metadataSuccess } from '../../../actions/pages';
import HtmlWidgetJson from '../../widgets/Html/HtmlWidget.meta';
import ListMetadata from '../List/ListMetadata.meta';
import SecureTabRegionJson from './TabsRegions.meta';
import AuthButtonContainer from '../../../core/auth/AuthLogin';
import configureStore from '../../../store';
import authProviderExample from '../../../../.storybook/auth/authProviderExample';
import { makeStore } from '../../../../.storybook/decorators/utils';
import cloneObject from '../../../utils/cloneObject';
import { InitWidgentsTabs } from 'N2oStorybook/json';
import fetchMock from 'fetch-mock';
import { getStubData } from 'N2oStorybook/fetchMock';

const stories = storiesOf('Регионы/Вкладки', module);

stories.addDecorator(withTests('Tabs'));

const TabsRegionJson = set(
  cloneObject(SecureTabRegionJson),
  'tabs',
  pullAt(cloneObject(SecureTabRegionJson).tabs, 0)
);

console.log(TabsRegionJson);

const { store } = makeStore();

stories
  .addWithJSX('Метаданные', () => {
    store.dispatch(metadataSuccess('Page', HtmlWidgetJson));

    return <TabsRegion {...TabsRegionJson} pageId="Page" />;
  })
  .addWithJSX('Ограничение доступа', () => {
    store.dispatch(metadataSuccess('Page', ListMetadata));
    return (
      <div>
        <small>
          Введите <mark>admin</mark>, чтобы увидеть скрытый виджет региона
        </small>
        <AuthButtonContainer />
        <br />
        <TabsRegion {...SecureTabRegionJson} pageId="Page" />
      </div>
    );
  })
  .addWithJSX('Инициализация виджетов', () => {
    fetchMock
      .restore()
      .get('begin:n2o/data/test', getStubData)
      .get('begin:n2o/data2/test', async url => {
        await new Promise(r =>
          setTimeout(() => {
            r();
          }, 2000)
        );
        return getStubData(url);
      });

    store.dispatch(metadataSuccess('Page', { ...pick(InitWidgentsTabs, 'widgets') }));

    return <TabsRegion {...omit(InitWidgentsTabs, 'widgets')} pageId="Page" />;
  });
