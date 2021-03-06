import { isObject, isArray, isBoolean, isFunction, each, isEmpty, find, pickBy, get } from 'lodash';
import { isPromise } from '../../tools/helpers';
import * as presets from './presets';
import { addFieldMessage } from '../../actions/formPlugin';

function findPriorityMessage(messages) {
  return (
    find(messages, { severity: 'danger' }) ||
    find(messages, { severity: 'warning' }) ||
    find(messages, { severity: 'success' })
  );
}

/**
 * есть ли ошибки или нет
 * @param messages
 * @returns {*}
 */
function hasError(messages) {
  return [].concat
    .apply([], Object.values(messages))
    .reduce((res, msg) => msg.severity === 'danger' || res, false);
}

export default function createValidator(validationConfig = {}, formName, store) {
  return {
    asyncValidate: validateField(validationConfig, formName, store),
    asyncChangeFields: Object.keys(validationConfig || {})
  };
}

/**
 * функция валидации
 * @param validationConfig
 * @param formName
 * @param store
 */
export const validateField = (validationConfig, formName, store) => (values, dispatch) => {
  const state = store && store.getState();
  const registeredFields = get(state, [formName, 'registeredFields']);
  const validation = pickBy(validationConfig, (value, key) =>
    get(registeredFields, `${key}.visible`, true)
  );

  const errors = {};
  const promiseList = [Promise.resolve()];
  const addError = (fieldId, { text = true, severity = true }, options = {}) => {
    !errors[fieldId] && (errors[fieldId] = []);
    errors[fieldId].push({});
    const last = errors[fieldId].length - 1;
    isBoolean(text)
      ? (errors[fieldId][last].text = options.text)
      : (errors[fieldId][last].text = text);
    isBoolean(severity)
      ? (errors[fieldId][last].severity = options.severity)
      : (errors[fieldId][last].severity = severity);
  };
  each(validation, (validationList, fieldId) => {
    if (isArray(validationList)) {
      each(validationList, options => {
        const isValid =
          isFunction(presets[options.type]) &&
          presets[options.type](fieldId, values, options, dispatch);
        if (isPromise(isValid)) {
          promiseList.push(
            new Promise(resolve => {
              isValid.then(
                resp => {
                  each(resp && resp.message, m => {
                    addError(fieldId, m, options);
                  });
                  resolve();
                },
                () => {
                  resolve();
                }
              );
            })
          );
        } else if (!isValid) {
          addError(fieldId, {}, options);
        }
      });
    }
  });
  return Promise.all(promiseList).then(() => {
    each(errors, (messages, fieldId) => {
      !isEmpty(messages) &&
        dispatch(addFieldMessage(formName, fieldId, findPriorityMessage(messages)));
    });
    return hasError(errors);
  });
};
