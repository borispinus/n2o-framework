import React from 'react';
import { Tooltip } from 'reactstrap';
import cn from 'classnames';
import { Progress, Alert } from 'reactstrap';
import { convertSize } from './utils';
import InlineSpinner from '../../snippets/Spinner/InlineSpinner';
import PropTypes from 'prop-types';

class FileUploaderItem extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      tooltipOpen: false
    };

    this.toggle = this.toggle.bind(this);
  }
  toggle() {
    this.setState({
      tooltipOpen: !this.state.tooltipOpen
    });
  }
  render() {
    const {
      file,
      percentage,
      statusBarColor,
      onRemove,
      showSize,
      disabled,
      index,
      loading,
      autoUpload
    } = this.props;
    return (
      <div className="n2o-file-uploader-files-item">
        <span className="n2o-file-uploader-files-item-info">
          <a
            href={file.link}
            target="_blank"
            id={file.id}
            className={cn('n2o-file-uploader-file-name', {
              'n2o-file-uploader-item-error': file.error
            })}
          >
            {file.name}
          </a>
          {file.error && (
            <Tooltip isOpen={this.state.tooltipOpen} target={file.id} toggle={this.toggle}>
              {file.error}
            </Tooltip>
          )}
          <span>
            {showSize && (
              <span className={'n2o-file-uploader-item-size'}>{convertSize(file.size)}</span>
            )}
            {!disabled &&
              !loading && (
                <i
                  onClick={() => onRemove(index, file.id)}
                  className={'n2o-file-uploader-remove fa fa-times'}
                />
              )}
            {loading && <InlineSpinner />}
          </span>
        </span>
        {loading ||
          (!autoUpload &&
            !file.status && (
              <Progress
                className="n2o-file-uploader-progress-bar"
                value={percentage}
                animated={true}
                color={statusBarColor}
              />
            ))}
      </div>
    );
  }
}

FileUploaderItem.propTypes = {
  file: PropTypes.object,
  percentage: PropTypes.number,
  statusBarColor: PropTypes.string,
  onRemove: PropTypes.func,
  showSize: PropTypes.bool,
  disabled: PropTypes.bool,
  error: PropTypes.bool,
  status: PropTypes.number,
  index: PropTypes.number,
  loading: PropTypes.bool
};

FileUploaderItem.defaultProps = {
  statusBarColor: 'success'
};

export default FileUploaderItem;
