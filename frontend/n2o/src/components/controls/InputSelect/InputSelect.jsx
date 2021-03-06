import React from 'react';
import PropTypes from 'prop-types';
import onClickOutside from 'react-onclickoutside';
import { Dropdown, DropdownToggle } from 'reactstrap';
import cx from 'classnames';
import InputSelectGroup from './InputSelectGroup';
import PopupList from './PopupList';
import InputContent from './InputContent';
import { find, isEqual, isEmpty, isNil } from 'lodash';
import Alert from '../../snippets/Alerts/Alert';
import Popup from './Popup';

/**
 * InputSelect
 * @reactProps {object} style - css стили
 * @reactProps {boolean} loading - флаг анимации загрузки
 * @reactProps {array} options - данные
 * @reactProps {string} valueFieldId - значение ключа value в данных
 * @reactProps {string} labelFieldId - значение ключа label в данных
 * @reactProps {string} iconFieldId - поле для иконки
 * @reactProps {string} imageFieldId - поле для картинки
 * @reactProps {string} badgeFieldId - поле для баджей
 * @reactProps {string} badgeColorFieldId - поле для цвета баджа
 * @reactProps {boolean} disabled - флаг неактивности
 * @reactProps {array} disabledValues - неактивные данные
 * @reactProps {string} filter - варианты фильтрации
 * @reactProps {string} value - текущее значение
 * @reactProps {function} onToggle
 * @reactProps {function} onInput - callback при вводе в инпут
 * @reactProps {function} onChange - callback при выборе значения или вводе
 * @reactProps {function} onSelect
 * @reactProps {function} onScrollENd - callback при прокрутке скролла popup
 * @reactProps {string} placeHolder - подсказка в инпуте
 * @reactProps {boolean} resetOnBlur - фича, при которой сбрасывается значение контрола, если оно не выбрано из popup
 * @reactProps {function} onOpen - callback на открытие попапа
 * @reactProps {function} onClose - callback на закрытие попапа
 * @reactProps {boolean} multiSelect - флаг мульти выбора
 * @reactProps {string} groupFieldId - поле для группировки
 * @reactProps {boolean} closePopupOnSelect - флаг закрытия попапа при выборе
 * @reactProps {boolean} hasCheckboxes - флаг наличия чекбоксов
 * @reactProps {string} format - формат
 * @reactProps {function} onSearch
 * @reactProps {boolean} expandPopUp
 * @reactProps {array} alerts
 */

class InputSelect extends React.Component {
  constructor(props) {
    super(props);
    const { value, options, valueFieldId, labelFieldId, multiSelect } = this.props;
    const valueArray = Array.isArray(value) ? value : value ? [value] : [];
    const input = value && !multiSelect ? value[labelFieldId] : '';
    this.state = {
      inputFocus: false,
      isExpanded: false,
      isInputSelected: false,
      value: valueArray,
      activeValueId: null,
      options,
      input
    };

    this._hideOptionsList = this._hideOptionsList.bind(this);
    this._handleItemSelect = this._handleItemSelect.bind(this);
    this._removeSelectedItem = this._removeSelectedItem.bind(this);
    this._setIsExpanded = this._setIsExpanded.bind(this);
    this._handleClick = this._handleClick.bind(this);
    this._clearSelected = this._clearSelected.bind(this);
    this._setNewInputValue = this._setNewInputValue.bind(this);
    this._setInputFocus = this._setInputFocus.bind(this);
    this._setActiveValueId = this._setActiveValueId.bind(this);
    this._handleValueChangeOnSelect = this._handleValueChangeOnSelect.bind(this);
    this._handleValueChangeOnBlur = this._handleValueChangeOnBlur.bind(this);
    this._handleDataSearch = this._handleDataSearch.bind(this);
    this._handleElementClear = this._handleElementClear.bind(this);
  }

  componentWillReceiveProps(nextProps) {
    const { multiSelect, value, valueFieldId, labelFieldId, options, loading } = nextProps;
    if (!isEqual(nextProps.options, this.state.options)) {
      this.setState({ options });
    }
    if (!isEqual(nextProps.value, this.props.value)) {
      const valueArray = Array.isArray(value) ? value : value ? [value] : [];
      const input = value && !multiSelect ? value[labelFieldId] : '';

      this.setState({ value: valueArray, input });
    }
  }

  /**
   * установить акстивный элемент дропдауна
   * @param activeValueId
   * @private
   */
  _setActiveValueId(activeValueId) {
    this.setState({ activeValueId });
  }

  /**
   * обработка изменения значения при потери фокуса(считаем, что при потере фокуса пользователь закончил вводить новое значение)
   * @private
   */
  _handleValueChangeOnBlur() {
    const { value, input, options } = this.state;
    const { onChange, multiSelect, resetOnBlur, labelFieldId } = this.props;
    const newValue = find(options, { [labelFieldId]: input });

    const findValue = find(value, [labelFieldId, input]);

    if (input && isEmpty(findValue)) {
      this.setState(
        {
          input: multiSelect ? '' : (value[0] && value[0][labelFieldId]) || '',
          value
        },
        () => onChange(this._getValue())
      );
    }
    if (!input && value.length) {
      this.setState(
        {
          input: '',
          value: multiSelect ? value : []
        },
        () => onChange(this._getValue())
      );
    }
  }

  /**
   * Обработка клика на инпут
   * @private
   */
  _handleClick() {
    // const searchCallback = () => {
    this._setIsExpanded(true);
    //};
    //this._handleDataSearch(this.state.input, false, searchCallback);
    this._setSelected(false);
    this._setInputFocus(true);
  }

  /**
   * Обработка изменения значения при выборе из дропдауна
   * @param item
   * @private
   */
  _handleValueChangeOnSelect(item) {
    const { value, input, options } = this.state;
    const { onChange, multiSelect, resetOnBlur, labelFieldId } = this.props;
    this.setState(
      {
        input: multiSelect ? item[labelFieldId] : '',
        value: multiSelect ? [...value, item] : [item]
      },
      () => {
        onChange(this._getValue());
      }
    );
  }

  /**
   * Возвращает текущее значение (массив - если ипут селект, объект - если нет)
   * или null если пусто
   * @returns {*}
   * @private
   */

  _getValue() {
    const { multiSelect } = this.props;
    const { value } = this.state;
    const rObj = multiSelect ? value : value[0];
    return rObj || null;
  }

  /**
   * Удаляет элемент из списка выбранных
   * @param item - элемент
   * @private
   */

  _removeSelectedItem(item) {
    const { onChange } = this.props;
    const value = this.state.value.filter(i => i.id !== item.id);
    this.setState({ value }, onChange(this._getValue()));
  }

  /**
   * Скрывает popUp
   * @private
   */

  _hideOptionsList() {
    this._setIsExpanded(false);
    this._setInputFocus(false);
  }

  /**
   * Очищает выбранный элемент
   * @private
   */

  _clearSelected() {
    const { onChange } = this.props;
    this.setState({ value: [], input: '' }, () => onChange(this._getValue()));
  }

  /**
   * установить / сбросить фокус
   * @param inputFocus
   * @private
   */
  _setInputFocus(inputFocus) {
    this.setState({ inputFocus });
  }

  /**
   * скрыть / показать попап
   * @param isExpanded
   * @private
   */
  _setIsExpanded(isExpanded) {
    const { disabled, onToggle, onClose, onOpen } = this.props;
    const { isExpanded: previousIsExpanded } = this.state;
    if (!disabled && isExpanded !== previousIsExpanded) {
      this.setState({ isExpanded });
      onToggle(isExpanded);
      isExpanded ? onOpen() : onClose();
    }
  }

  /**
   * выделить текст
   * @param isInputSelected
   * @private
   */
  _setSelected(isInputSelected) {
    this.setState({ isInputSelected });
  }

  /**
   * Выполняет поиск элементов для popUp, если установлен фильтр
   * @param newValue - значение для поиска
   * @private
   */

  _handleDataSearch(input, delay = true, callback) {
    const { onSearch, filter, labelFieldId, options } = this.props;
    if (filter && ['includes', 'startsWith', 'endsWith'].includes(filter)) {
      const filterFunc = item => String.prototype[filter].call(item, input);
      const filteredData = options.filter(item => filterFunc(item[labelFieldId]));
      this.setState({ options: filteredData });
    } else {
      //серверная фильтрация
      const labels = this.state.value.map(item => item[labelFieldId]);
      if (labels.some(label => label === input)) {
        onSearch('', delay, callback);
      } else {
        onSearch(input, delay, callback);
      }
    }
  }

  /**
   * новое значение инпута search)
   * @param input
   * @private
   */
  _setNewInputValue(input) {
    const { onInput, resetOnBlur, multiSelect } = this.props;
    const { value } = this.state;
    const onSetNewInputValue = input => {
      onInput(input);
      this._handleDataSearch(input);
    };

    if (this.state.input !== input) {
      this._setSelected(false);
      this.setState({ input }, () => onSetNewInputValue(input));
    }
  }

  /**
   * Обрабатывает выбор элемента из popUp
   * @param item - элемент массива options
   * @private
   */

  _handleItemSelect(item) {
    const {
      multiSelect,
      closePopupOnSelect,
      labelFieldId,
      options,
      onSelect,
      onChange
    } = this.props;
    const selectCallback = () => {
      closePopupOnSelect && this._hideOptionsList();
      onSelect(item);
      onChange(this._getValue());
      this._setSelected(true);
    };

    this.setState(
      prevState => ({
        value: multiSelect ? [...prevState.value, item] : [item],
        input: multiSelect ? '' : item[labelFieldId],
        options
      }),
      selectCallback
    );
  }

  /**
   * Очищает инпут и результаты поиска
   * @private
   */

  _clearSearchField() {
    this.setState({ input: '', options: this.props.options }, this._handleDataSearch);
  }

  /**
   * Очищениеб сброс фокуса, выделенного значения
   * @private
   */
  _handleElementClear() {
    if (!this.props.disabled) {
      this._clearSearchField();
      this._clearSelected();
      this._setInputFocus(false);
    }
  }

  /**
   * Обрабатывает клик за пределы компонента
   * @param evt
   */

  handleClickOutside(evt) {
    const { resetOnBlur } = this.props;
    this._hideOptionsList();
    resetOnBlur && this._handleValueChangeOnBlur();
  }

  /**
   * Рендер
   */
  render() {
    const {
      loading,
      className,
      valueFieldId,
      labelFieldId,
      iconFieldId,
      disabled,
      placeholder,
      multiSelect,
      disabledValues,
      imageFieldId,
      groupFieldId,
      hasCheckboxes,
      format,
      badgeFieldId,
      badgeColorFieldId,
      onScrollEnd,
      expandPopUp,
      style,
      alerts
    } = this.props;
    const inputSelectStyle = { width: '100%', cursor: 'text', ...style };
    return (
      <Dropdown
        style={inputSelectStyle}
        className={cx('n2o-input-select', { disabled })}
        toggle={() => {}}
        onBlur={() => {
          this._setInputFocus(false);
          this._setSelected(false);
          this.props.onBlur();
        }}
        onFocus={() => {
          this._setInputFocus(true);
          this._setSelected(true);
        }}
        isOpen={this.state.isExpanded && !disabled}
      >
        <DropdownToggle tag="div" disabled={disabled}>
          <InputSelectGroup
            isExpanded={this.state.isExpanded}
            setIsExpanded={this._setIsExpanded}
            loading={loading}
            selected={this.state.value}
            input={this.state.input}
            iconFieldId={iconFieldId}
            imageFieldId={imageFieldId}
            multiSelect={multiSelect}
            isInputInFocus={this.state.inputFocus}
            onClearClick={this._handleElementClear}
            disabled={disabled}
            className={className}
          >
            <InputContent
              loading={loading}
              value={this.state.input}
              disabled={disabled}
              disabledValues={disabledValues}
              valueFieldId={valueFieldId}
              placeholder={placeholder}
              options={this.state.options}
              openPopUp={() => this._setIsExpanded(true)}
              closePopUp={() => this._setIsExpanded(false)}
              onInputChange={this._setNewInputValue}
              onRemoveItem={this._removeSelectedItem}
              isExpanded={this.state.isExpanded}
              isSelected={this.state.isInputSelected}
              inputFocus={this.state.inputFocus}
              iconFieldId={iconFieldId}
              activeValueId={this.state.activeValueId}
              setActiveValueId={this._setActiveValueId}
              imageFieldId={imageFieldId}
              selected={this.state.value}
              labelFieldId={labelFieldId}
              clearSelected={this._clearSelected}
              multiSelect={multiSelect}
              onClick={this._handleClick}
              onSelect={this._handleItemSelect}
            />
          </InputSelectGroup>
        </DropdownToggle>
        <Popup isExpanded={this.state.isExpanded} expandPopUp={expandPopUp}>
          <PopupList
            isExpanded={this.state.isExpanded}
            activeValueId={this.state.activeValueId}
            setActiveValueId={this._setActiveValueId}
            onScrollEnd={onScrollEnd}
            options={this.state.options}
            valueFieldId={valueFieldId}
            labelFieldId={labelFieldId}
            iconFieldId={iconFieldId}
            imageFieldId={imageFieldId}
            badgeFieldId={badgeFieldId}
            badgeColorFieldId={badgeColorFieldId}
            onSelect={this._handleItemSelect}
            selected={this.state.value}
            disabledValues={disabledValues}
            groupFieldId={groupFieldId}
            hasCheckboxes={hasCheckboxes}
            onRemoveItem={this._removeSelectedItem}
            format={format}
            inputSelect={this.inputSelect}
          >
            <div className="n2o-alerts">
              {alerts &&
                alerts.map(alert => (
                  <Alert
                    key={alert.id}
                    onDismiss={() => this.props.onDismiss(alert.id)}
                    {...alert}
                  />
                ))}
            </div>
          </PopupList>
        </Popup>
      </Dropdown>
    );
  }
}

InputSelect.propTypes = {
  style: PropTypes.object,
  loading: PropTypes.bool,
  options: PropTypes.array.isRequired,
  valueFieldId: PropTypes.string.isRequired,
  labelFieldId: PropTypes.string.isRequired,
  iconFieldId: PropTypes.string,
  imageFieldId: PropTypes.string,
  badgeFieldId: PropTypes.string,
  badgeColorFieldId: PropTypes.string,
  disabled: PropTypes.bool,
  disabledValues: PropTypes.array,
  filter: PropTypes.oneOf(['includes', 'startsWith', 'endsWith', false]),
  value: PropTypes.oneOfType([PropTypes.array, PropTypes.object]),
  onToggle: PropTypes.func,
  onInput: PropTypes.func,
  onChange: PropTypes.func,
  onSelect: PropTypes.func,
  onScrollEnd: PropTypes.func,
  placeholder: PropTypes.string,
  resetOnBlur: PropTypes.bool,
  onOpen: PropTypes.func,
  onClose: PropTypes.func,
  multiSelect: PropTypes.bool,
  groupFieldId: PropTypes.string,
  closePopupOnSelect: PropTypes.bool,
  hasCheckboxes: PropTypes.bool,
  format: PropTypes.string,
  onSearch: PropTypes.func,
  expandPopUp: PropTypes.bool,
  alerts: PropTypes.array
};

InputSelect.defaultProps = {
  valueFieldId: 'id',
  labelFieldId: 'name',
  iconFieldId: 'icon',
  imageFieldId: 'image',
  badgeFieldId: 'badge',
  loading: false,
  disabled: false,
  disabledValues: [],
  resetOnBlur: false,
  filter: false,
  multiSelect: false,
  closePopupOnSelect: true,
  hasCheckboxes: false,
  expandPopUp: false,
  onSearch() {},
  onSelect() {},
  onToggle() {},
  onInput() {},
  onOpen() {},
  onClose() {},
  onChange() {},
  onScrollEnd() {},
  onBlur() {}
};

export default onClickOutside(InputSelect);
