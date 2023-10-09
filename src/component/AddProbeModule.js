import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import '../sass/ProbeDetails/AddProbeModule.scss'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleXmark, faFolderOpen, faNoteSticky } from '@fortawesome/free-regular-svg-icons'
import {
    faFloppyDisk, faCube, faTerminal
} from '@fortawesome/free-solid-svg-icons'
import 'react-toastify/dist/ReactToastify.css';

const AddProbeModule = ({ handleCloseWindow,notify }) => {

    const [isOpen, openCloseWindow] = useState(true)
    const addModule =()=>{
        setTimeout(notify(),2000)
        handleCloseWindow()
    }
    return (
        <div>
            {isOpen && (<div className='addProbeModuleScreen'>
                <div className="addProbe">
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='close-btn d-flex align-items-center' onClick={handleCloseWindow}>
                            <FontAwesomeIcon icon={faCircleXmark} />
                        </button>
                    </div>
                    {/* Module & Command */}
                    <div className="field ">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faCube} />
                                <div className='input_container-icon-text'>MODULE</div>
                            </div>
                            <div className='input_container-input'>
                                <select id='modules'>
                                    <option>Kiểm tra tốc độ mạng</option>
                                    <option>Module 1</option>
                                    <option>Kiểm tra địa chỉ IP</option>
                                </select>
                            </div>
                        </div>
                        <div className="field ">
                            <div className='input_container exception'>
                                <div className='input_container-icon d-flex align-items-center'>
                                    <FontAwesomeIcon icon={faTerminal} />
                                    <div className='input_container-icon-text'>COMMAND</div>
                                </div>
                                <div className='input_container-input'>
                                    <textarea className='commandInput' placeholder='Command here...' id='command'></textarea>
                                </div>
                                <div className='input_container-input'>
                                    <input className='commandInput' type='text' placeholder='Path...' id='path'></input>
                                </div>
                                <div className='input_container-input'>
                                    <input className='commandInput exception' type='text' placeholder='Your argument' id='argument'></input>
                                </div>
                            </div>
                        </div>
                    </div>
                    {/* IP Address & Password */}
                    <div className="field">
                        <div className='input_container'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faFolderOpen} />
                                <div className='input_container-icon-text'>LOG PATH</div>
                            </div>
                            <div className='input_container-input'>
                                <input type='text' placeholder='Log path here....' id='ip_address'></input>
                            </div>
                        </div>
                    </div>
                    {/*Note*/}
                    <div className="field d-flex justify-content-between">
                        <div className='input_container exception'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faNoteSticky} />
                                <div className='input_container-icon-text'>NOTE</div>
                            </div>
                            <div className='input_container-input'>
                                <textarea placeholder='Take note here...' id='note'></textarea>
                            </div>
                        </div>
                    </div>
                    {/* Button */}
                    <div className='btn-container d-flex justify-content-end'>
                        <button className='btn btn-success d-flex align-items-center' onClick={addModule} >
                            <div className='btn-icon d-flex align-items-center' >
                                <FontAwesomeIcon icon={faFloppyDisk} />
                            </div>
                            <div className='btn-text' >Save</div>
                        </button>
                    </div>
                </div>
            </div>)
            }
        </div>
    )
}
export default AddProbeModule;