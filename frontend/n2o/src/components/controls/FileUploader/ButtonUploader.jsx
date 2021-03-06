import React from 'react';
import withFileUploader from './withFileUploader';
import FileUploader from './FileUploader';
import { isEmpty } from 'lodash';
import PropTypes from 'prop-types';
import { Button } from 'reactstrap';
import cn from 'classnames';

function createButtonUploaderChildren(icon, label, children) {
  return (
    <Button>
      {children ? (
        children
      ) : (
        <React.Fragment>
          <div className={cn('n2o-file-uploader-icon', { [icon]: icon })} />
          <span>{label}</span>
        </React.Fragment>
      )}
    </Button>
  );
}

class ButtonUploader extends React.Component {
  render() {
    const { children, icon, label } = this.props;
    const childrenComponent = createButtonUploaderChildren(icon, label, children);
    return (
      <FileUploader
        {...this.props}
        children={childrenComponent}
        componentClass={'n2o-button-uploader'}
      />
    );
  }
}

ButtonUploader.propTypes = {
  label: PropTypes.string,
  uploading: PropTypes.object,
  icon: PropTypes.string,
  files: PropTypes.arrayOf(PropTypes.object),
  className: PropTypes.string,
  onDrop: PropTypes.func,
  onRemove: PropTypes.func,
  autoUpload: PropTypes.bool,
  visible: PropTypes.bool,
  disabled: PropTypes.bool,
  name: PropTypes.string,
  maxSize: PropTypes.number,
  minSize: PropTypes.number,
  multiple: PropTypes.bool,
  onChange: PropTypes.func,
  statusBarColor: PropTypes.string,
  showSize: PropTypes.bool,
  children: PropTypes.oneOf(PropTypes.node, PropTypes.func)
};

export default withFileUploader(ButtonUploader);
