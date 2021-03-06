import React from 'react';
import PropTypes from 'prop-types';
import { map, get } from 'lodash';
import { ButtonGroup } from 'reactstrap';
import HintButton from './HintButton';
import HintDropDown from './HintDropDown';
import cx from 'classnames';
import withCell from '../../withCell';

/**
 *
 * @param id
 * @param className
 * @param callActionImpl
 * @param buttons
 * @param other
 * @returns {*}
 * @constructor
 */
function ButtonsCell({ id, className, callActionImpl, buttons, ...other }) {
  const handlerClick = action => callActionImpl({ action });

  const createGroupItems = ({ subMenu, ...rest }) =>
    subMenu ? (
      <HintDropDown menu={subMenu} onClick={handlerClick} {...rest} />
    ) : (
      <HintButton onClick={handlerClick} {...rest} />
    );

  return (
    <ButtonGroup key={id} className={cx('n2o-table-btn', className)} {...other}>
      {map(buttons, createGroupItems)}
    </ButtonGroup>
  );
}

ButtonsCell.propTypes = {
  size: PropTypes.string,
  className: PropTypes.string,
  style: PropTypes.object,
  id: PropTypes.string
};

ButtonsCell.defaultProps = {
  size: 'sm'
};

export default withCell(ButtonsCell);
