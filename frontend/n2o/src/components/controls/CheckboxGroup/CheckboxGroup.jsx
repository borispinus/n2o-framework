import React from 'react';
import PropTypes from 'prop-types';
import cx from 'classnames';
import { xorBy } from 'lodash';

/**
 * Компонент - группа чекбоксов, содержит {@link Checkbox} как children
 * @reactProps {array} value - выбранное значение
 * @reactProps {function} onChange - вызывается при изменении значения
 * @reactProps {boolean} disabled - флаг блокировки
 * @reactProps {boolean} visible - флаг видимости
 * @reactProps {boolean} inline - флаг вывода в ряд
 * @reactProps {object} style - стили группы
 * @reactProps {string} className - класс группы
 * @reactProps {node} children - элемент потомок копонента CheckboxGroup
 * @example
 * <CheckboxGroup value={ ['apple', 'orange'] } onChange={(val)=> console.log(val)}>
 * <Checkbox value="apple" compileLabel='Apple' />
 * <Checkbox value="orange" compileLabel='Orange'/>
 * <Checkbox value="watermelon" compileLabel='Watermelon'/>
 * </CheckboxGroup>
 **/

class CheckboxGroup extends React.Component {
  constructor(props) {
    super(props);

    this._onChange = this._onChange.bind(this);
  }

  /**
   * Обработчик изменения чекбокса
   * @param e - событие
   * @private
   */

  _onChange(e) {
    const { onChange, value, valueFieldId } = this.props;
    const { value: newValue } = e.target;
    onChange(xorBy(value, [newValue], valueFieldId));
  }

  /**
   * Рендер
   */

  render() {
    const { children, visible, inline, style, className, value } = this.props;

    const element = child => {
      return React.cloneElement(child, {
        checked: value.includes(child.props.value),
        disabled: this.props.disabled || child.props.disabled,
        onChange: this._onChange,
        inline: this.props.inline
      });
    };

    const isCheckboxChild = child => {
      const checkboxTypes = ['Checkbox', 'CheckboxN2O', 'CheckboxButton'];
      return child.type && checkboxTypes.includes(child.type.displayName);
    };

    const isBtn =
      children &&
      React.Children.map(children, child => child.type.displayName).includes('CheckboxButton');

    return (
      <React.Fragment>
        {visible !== false && (
          <div
            className={cx(className, {
              [`btn-group${inline ? '' : '-vertical'}`]: isBtn,
              'btn-group-toggle': isBtn,
              'n2o-checkbox-inline': inline
            })}
            style={style}
          >
            {children &&
              React.Children.map(children, child => {
                if (isCheckboxChild(child)) {
                  return element(child);
                }
              })}
          </div>
        )}
      </React.Fragment>
    );
  }
}

CheckboxGroup.propTypes = {
  value: PropTypes.array,
  onChange: PropTypes.func,
  disabled: PropTypes.bool,
  visible: PropTypes.bool,
  children: PropTypes.node.isRequired,
  inline: PropTypes.bool,
  style: PropTypes.object,
  className: PropTypes.string
};

CheckboxGroup.defaultProps = {
  value: [],
  visible: true
};

export default CheckboxGroup;
