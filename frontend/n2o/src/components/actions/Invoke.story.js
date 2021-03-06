import React from 'react';
import { storiesOf } from '@storybook/react';

import { getStubData } from 'N2oStorybook/fetchMock';
import fetchMock from 'fetch-mock';
import Factory from '../../core/factory/Factory';
import { WIDGETS } from '../../core/factory/factoryLevels';
import metadata from './Invoke.meta';
import Actions from './Actions';

const stories = storiesOf('Действия/POST запрос', module);
const urlPattern = 'begin:n2o/data';

stories
  .addWithJSX('Компонент', () => {
    const toolbar = [
      {
        buttons: [
          {
            id: 'testInvoke',
            title: 'Пример Invoke',
            actionId: 'invoke',
            hint: 'Отправить invoke'
          }
        ]
      }
    ];
    return (
      <Actions
        actions={metadata.Page_Table.actions}
        toolbar={toolbar}
        containerKey={'actionExample'}
      />
    );
  })
  .add('Метаданные', () => {
    fetchMock
      .restore()
      .get(urlPattern, url => getStubData(url))
      .post(urlPattern, url => {
        return {
          meta: {
            alert: {
              alertKey: 'Page_Table',
              messages: [
                {
                  id: 'test',
                  severity: 'success',
                  text: 'Invoke прошел успешно'
                }
              ]
            }
          }
        };
      });

    return <Factory level={WIDGETS} {...metadata['Page_Table']} id="Page_Table" />;
  });
