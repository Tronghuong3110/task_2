import React, { useState, useEffect, useRef } from 'react';
import '../../sass/DropDownInput.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
    faLocationDot, faChartArea, faNetworkWired
} from '@fortawesome/free-solid-svg-icons'
const DropDownInput = (props) => {
    const {defaultContent,inputOptions,handleSelect,needAll} = props
    const [isPopupOpen, setIsPopupOpen] = useState(false);
    const [customInputValue, setCustomInputValue] = useState('');
    const [options, setOptions] = useState([]);
    const [renderInputValue,setRenderInputValue] = useState("")
    const dropdownRef = useRef(null)
    useEffect(() => {
        const handleOutsideClick = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsPopupOpen(false);
            }
        }

        document.addEventListener('click', handleOutsideClick);

        return () => {
            document.removeEventListener('click', handleOutsideClick);
        }
    }, []);
    useEffect(()=>{
        setOptions(inputOptions)
    })
    const filteredOptions = options.filter(option => {
        return option.label.toLowerCase().includes(customInputValue.toLowerCase());
    });
    const handleInputChange = (e) => {
        setCustomInputValue(e.target.value);
    }

    const togglePopup = () => {
        setIsPopupOpen(!isPopupOpen);
    }

    const handleOptionSelect = (option) => {
        handleSelect(option.value)
        setRenderInputValue(option.label)
        setCustomInputValue("");
        setIsPopupOpen(false);
    }

    return (
        <div className="dropdown" ref={dropdownRef}>
            <div className="select" onClick={togglePopup}>
                {renderInputValue||defaultContent}
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
                        { !needAll && (<div
                            key={"---.---"}
                            className="option"
                            onClick={() => handleOptionSelect({
                                value: null,
                                label: "---.---"
                            })}
                        >
                            {"---.---"}
                        </div>)}
                        {filteredOptions.map(option => (
                            <div
                                key={option.value}
                                className="option"
                                onClick={() => handleOptionSelect({
                                    label: option.label,
                                    value :option.value
                                })}
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

export default DropDownInput;
