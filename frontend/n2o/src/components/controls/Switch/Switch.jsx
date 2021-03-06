import React from 'react';
import PropTypes from 'prop-types';
import { isNil } from 'lodash';
import Switch from 'rc-switch';

class N2OSwitch extends React.Component {
  /**
   * базовый рендер
   * */
  render() {
    const { disabled, value, checked, onChange } = this.props;

    return (
      <div className="n2o-switch-wrapper">
        <Switch
          prefixCls="n2o-switch"
          checked={isNil(checked) ? !!value : checked}
          disabled={disabled}
          onChange={onChange}
        />
      </div>
    );
  }
}

N2OSwitch.propTypes = {
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number, PropTypes.bool]),
  checked: PropTypes.bool,
  disabled: PropTypes.bool,
  onChange: PropTypes.func
};

export default N2OSwitch;
