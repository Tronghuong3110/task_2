import React, { useState, useEffect } from 'react';
import '../sass/DropdownWithInput.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faLocationDot, faChartArea, faNetworkWired
} from '@fortawesome/free-solid-svg-icons'
import { type } from '@testing-library/user-event/dist/type';
const DropdownWithInput = (props) => {
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const [customInputValue, setCustomInputValue] = useState('');
  const [options, setOptions] = useState([]);
  const [renderInputValue, setRenderInputValue] = useState('')
  const typeSelect = {
    'Search location': faLocationDot,
    'Search area': faChartArea,
    'Search VLAN': faNetworkWired
  }
  useEffect(() => {
    const updatedOptions = props.options.map(element => ({
      value: element.code,
      label: element.name
    }));
    setOptions(updatedOptions);
  }, [props.options]);

  const filteredOptions = options.filter(option => {
    return option.label.toLowerCase().includes(customInputValue.toLowerCase());
  });
  const handleInputChange = (e) => {
    setCustomInputValue(e.target.value);
  }

  const togglePopup = () => {
    setIsPopupOpen(!isPopupOpen);
  }

  const handleOptionSelect = (value) => {
    setRenderInputValue(value)
    setCustomInputValue("");
    setIsPopupOpen(false);
    if(props.type!='Search VLAN') props.onOptionSelect(value);

  }

  return (
    <div className="dropdown">
      <div className="select" onClick={togglePopup}>
        <FontAwesomeIcon icon={typeSelect[props.type]} style={{ color: "#ffffff", padding: "0 10px" }} />
        {renderInputValue || props.type}
      </div>
      {isPopupOpen && (
        <div className="popup" style={{ zIndex: "2" }}>
          <div className='filterInput'>
            <input
              type="text"
              value={customInputValue}
              onChange={handleInputChange}
              placeholder="Filter options"
              id="filter"
            />
          </div>
          <div className="options">
            { !props.edit && (<div
              key={"---.---"}
              className="option"
              onClick={() => handleOptionSelect("---.---")}
            >
              {"---.---"}
            </div>)}
            {filteredOptions.map(option => (
              <div
                key={option.value}
                className="option"
                onClick={() => handleOptionSelect(option.label)}
              >
                {option.label}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default DropdownWithInput;
