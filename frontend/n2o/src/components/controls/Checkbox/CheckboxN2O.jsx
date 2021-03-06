import React from 'react';
import PropTypes from 'prop-types';
import { isNil, uniqueId } from 'lodash';
import cx from 'classnames';
import { setDisplayName } from 'recompose';
import Input from '../Input/Input';

/**
 * Альтернативный чекбокс
 * @reactProps {string|number} value - уникально определяет элемент
 * @reactProps {boolean} checked - начальное значение
 * @reactProps {boolean} disabled - только для чтения / нет
 * @reactProps {function} onChange - вызывается при изменении значения
 * @reactProps {function} onClick - событие клика по чекбоксу
 * @reactProps {string} label - лейбл
 * @reactProps {string} className - класс копонента CheckboxN2O
 * @reactProps {boolean} inline - в ряд
 */

class CheckboxN2O extends React.Component {
  constructor(props) {
    super(props);

    this.elementId = uniqueId('checkbox-');
  }

  /**
   * Рендер
   */

  render() {
    const { className, label, disabled, value, onChange, inline, checked, onClick } = this.props;
    return (
      <div
        className={cx('custom-control', 'custom-checkbox', className, {
          'custom-control-inline': inline
        })}
      >
        <Input
          id={this.elementId}
          className="custom-control-input"
          disabled={disabled}
          type="checkbox"
          value={value}
          checked={isNil(checked) ? !!value : checked}
          onChange={onChange}
          onClick={onClick}
        />
        <label className="custom-control-label" htmlFor={this.elementId}>
          {label}
        </label>
      </div>
    );
  }
}

CheckboxN2O.propTypes = {
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func,
  onClick: PropTypes.func,
  disabled: PropTypes.bool,
  label: PropTypes.string,
  inline: PropTypes.bool,
  className: PropTypes.string,
  checked: PropTypes.bool
};

CheckboxN2O.defaultProps = {
  disabled: false,
  inline: false
};

export default setDisplayName('CheckboxN2O')(CheckboxN2O);
