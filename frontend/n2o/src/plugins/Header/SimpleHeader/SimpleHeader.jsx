import React from 'react';
import PropTypes from 'prop-types';
import {
  Navbar,
  Nav,
  NavItem,
  InputGroup,
  InputGroupAddon,
  Input,
  NavbarBrand,
  NavbarToggler,
  Collapse
} from 'reactstrap';
import { isEmpty } from 'lodash';
import SecurityCheck from '../../../core/auth/SecurityCheck';

import NavbarBrandContent from './NavbarBrandContent';
import NavItemContainer from './NavItemContainer';

/**
 * Хедер-плагин
 * @param {Object} props - пропсы
 * @param {string|element} props.brand - брэнд
 * @param {string|element} props.brandImage - изображение брэнда
 * @param {array} props.items - элементы навбар-меню (левое меню)
 * @param {boolean} props.fixed - фиксированный хэдер или нет
 * @param {array} props.extraItems - элементы навбар-меню (правое меню)
 * @param {boolean} props.collapsed - находится в состоянии коллапса или нет
 * @param {boolean} props.search - есть поле поиска / нет поля поиска
 * @param {boolean} props.color - стиль хэдера (default или inverse)
 * @param {boolean} props.className - css-класс
 * @param {boolean} props.style - объект стиля
 * @example
 * //каждый item состоит из id {string}, label {string}, type {string} ('text', 'type' или 'dropdown'),
 * //href {string}(для ссылок), linkType {string}(для ссылок; значения - 'outer' или 'inner')
 * //subItems {array} (массив из элементов дропдауна)
 *<SimpleHeader  items = { [
 *     {
 *       id: 'link',
 *       label: 'link',
 *       href: '/test',
 *       type: 'link',
 *     },
 *     {
 *       id: 'dropdown',
 *       label: 'dropdown',
 *       type: 'dropdown',
 *       subItems: [{id: 'test1',label: 'test1', href: '/'}, {id: 'test123', label: 'test1', href: '/'}]
 *     },
 *     {
 *       id: 'test',
 *       label: 'test',
 *       type: 'dropdown',
 *       subItems: [{id: 'test123s',label: 'test1', href: '/'}, {id: 'test12asd3',label: 'test1', href: '/'}]
 *     }
 *     ] }
 *     extraItems = { [
 *     {
 *       id: "213",
 *       label: 'ГКБ №7',
 *       type: 'text',
 *     },
 *     {
 *       id: "2131",
 *       label: 'Постовая медсестра',
 *       type: 'dropdown',
 *       subItems: [{label: 'test1', href: '/', linkType: 'inner'}, {label: 'test1', href: '/'}]
 *     },
 *     {
 *       id: "2131",
 *       label: 'admin',
 *       type: 'dropdown',
 *       subItems: [{label: 'test1', href: '/'}, {label: 'test1', href: '/'}]
 *     }
 *     ] }
 *    brand="N2O"
 *    brandImage= "http://getbootstrap.com/assets/brand/bootstrap-solid.svg"
 *    activeId={"test123"}/>
 *
 */

class SimpleHeader extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isOpen: false
    };

    this.toggle = this.toggle.bind(this);
  }

  toggle() {
    this.setState({
      isOpen: !this.state.isOpen
    });
  }

  render() {
    const {
      color,
      fixed,
      items,
      activeId,
      extraItems,
      brandImage,
      brand,
      style,
      className,
      search
    } = this.props;
    const isInversed = color === 'inverse';
    const navColor = isInversed ? 'primary' : 'light';
    const mapItems = (items, options) =>
      items.map((item, i) => (
        <NavItemContainer key={i} item={item} activeId={activeId} options={options} />
      ));

    const navItems = mapItems(items);
    const extraNavItems = mapItems(extraItems, { right: true });

    return (
      <div
        style={style}
        className={`navbar-container-${
          fixed ? 'fixed' : 'relative'
        } ${className} n2o-header n2o-header-${color} `}
      >
        <Navbar color={navColor} light={!isInversed} dark={isInversed} expand="md">
          <NavbarBrand href="/">
            <NavbarBrandContent brand={brand} brandImage={brandImage} />
          </NavbarBrand>
          <NavbarToggler onClick={this.toggle} />
          <Collapse isOpen={this.state.isOpen} navbar>
            <Nav navbar>{navItems}</Nav>
            <Nav className="ml-auto" navbar>
              {extraNavItems}
              {search && (
                <NavItem>
                  <InputGroup>
                    <Input placeholder="Поиск" />
                    <InputGroupAddon addonType="append">
                      <span className="input-group-text">
                        <i className="fa fa-search" aria-hidden="true" />
                      </span>
                    </InputGroupAddon>
                  </InputGroup>
                </NavItem>
              )}
            </Nav>
          </Collapse>
        </Navbar>
      </div>
    );
  }
}

SimpleHeader.propTypes = {
  activeId: PropTypes.string.isRequired,
  brand: PropTypes.oneOfType([PropTypes.string, PropTypes.element]),
  brandImage: PropTypes.oneOfType([PropTypes.string, PropTypes.element]),
  items: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      href: PropTypes.string,
      linkType: PropTypes.oneOf(['inner', 'outer']),
      type: PropTypes.oneOf(['dropdown', 'link', 'text']).isRequired,
      subItems: PropTypes.array
    })
  ),
  extraItems: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      href: PropTypes.string,
      linkType: PropTypes.oneOf(['inner', 'outer']),
      type: PropTypes.oneOf(['dropdown', 'link', 'text']).isRequired,
      subItems: PropTypes.array
    })
  ),
  search: PropTypes.bool,
  color: PropTypes.oneOf(['inverse', 'default']),
  fixed: PropTypes.bool,
  collapsed: PropTypes.bool,
  className: PropTypes.string,
  style: PropTypes.object
};

SimpleHeader.defaultProps = {
  color: 'default',
  fixed: true,
  collapsed: true,
  className: '',
  items: [],
  extraItems: [],
  search: false,
  style: {}
};

export default SimpleHeader;
